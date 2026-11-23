package com.coliving.platform.chat.controller;

import com.coliving.platform.chat.dto.ChatMessageSendRequestDTO;
import com.coliving.platform.chat.service.ChatService;
import com.coliving.platform.common.exception.CommunityException;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * 4-5 WebSocket 메시지 보내기 — SEND /app/chat/{roomId} { "content": "..." }
 * 성공하면 저장 후 /user/queue/chat 으로 실시간 전송 (ChatMessagePushListener)
 * 실패하면 보낸 사람에게만 /user/queue/errors { code, message, roomId }
 *
 * @author 오시문
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

	private final ChatService chatService;

	/**
	 * 채팅 메시지를 WebSocket으로 보낸다.
	 * 회원이 /app/chat/{roomId}로 전송한다. 저장 후 /user/queue/chat으로 전달하며 상대가 나를 차단했으면 발신자에게만 전송한다. 오류는 /user/queue/errors로 보낸다.
	 *
	 * @param roomId 채팅방 ID (예: 1)
	 * @param request 보낼 메시지 내용 (공백만 입력 불가, 1~1000자)
	 * @param principal STOMP 인증으로 확인한 발신 회원 ID를 담은 Principal
	 * @throws com.coliving.platform.common.exception.CommunityException 403 CHAT_NOT_AVAILABLE 채팅 불가,
	 *         403 CHAT_READ_ONLY 읽기 전용 방,
	 *         403 SANCTIONED 제재 중,
	 *         404 ROOM_NOT_FOUND 참여 중인 채팅방 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@MessageMapping("/chat/{roomId}")
	public void send(@DestinationVariable Long roomId, @Valid @Payload ChatMessageSendRequestDTO request,
			Principal principal) {
		chatService.sendMessage(Long.valueOf(principal.getName()), roomId, request.content());
	}

	/**
	 * 채팅 업무 오류를 발신자에게 전달한다.
	 * /app/chat/{roomId} 처리 중 발생한 업무 오류를 /user/queue/errors로 보낸다. /user/queue/chat에는 성공 메시지를 보내지 않는다.
	 *
	 * @param e 처리 중 발생한 예외
	 * @param accessor 요청한 /app/chat/{roomId} 경로를 확인할 STOMP 헤더
	 * @return /user/queue/errors로 보낼 오류 코드·설명·채팅방 ID와 제재 부가 정보
	 */
	@MessageExceptionHandler(CommunityException.class)
	@SendToUser(destinations = "/queue/errors", broadcast = false)
	public Map<String, Object> handleCommunityException(CommunityException e, SimpMessageHeaderAccessor accessor) {
		Map<String, Object> body = error(e.getErrorCode().name(), e.getBody().getDetail(), accessor);
		if (e.getBody().getProperties() != null) {
			e.getBody().getProperties().forEach((key, value) -> {
				if (!"code".equals(key)) {
					body.put(key, value);   // SANCTIONED 의 sanctionType·endsAt 등
				}
			});
		}
		return body;
	}

	/**
	 * 채팅 요청 검증 오류를 발신자에게 전달한다.
	 * /app/chat/{roomId}의 본문 검증 실패 시 /user/queue/errors로 INVALID_REQUEST를 보낸다.
	 *
	 * @param e 처리 중 발생한 예외
	 * @param accessor 요청한 /app/chat/{roomId} 경로를 확인할 STOMP 헤더
	 * @return /user/queue/errors로 보낼 INVALID_REQUEST와 검증 실패 설명
	 */
	@MessageExceptionHandler(MethodArgumentNotValidException.class)
	@SendToUser(destinations = "/queue/errors", broadcast = false)
	public Map<String, Object> handleValidation(MethodArgumentNotValidException e, SimpMessageHeaderAccessor accessor) {
		String message = e.getBindingResult() != null && e.getBindingResult().getFieldError() != null
				? e.getBindingResult().getFieldError().getDefaultMessage()
				: "요청 값이 올바르지 않습니다.";
		return error("INVALID_REQUEST", message, accessor);
	}

	/**
	 * 예상하지 못한 채팅 오류를 발신자에게 전달한다.
	 * /app/chat/{roomId} 처리 중 그 밖의 예외가 발생하면 /user/queue/errors로 INTERNAL_ERROR를 보낸다.
	 *
	 * @param e 처리 중 발생한 예외
	 * @param accessor 요청한 /app/chat/{roomId} 경로를 확인할 STOMP 헤더
	 * @return /user/queue/errors로 보낼 INTERNAL_ERROR와 안내 문구
	 */
	@MessageExceptionHandler(Exception.class)
	@SendToUser(destinations = "/queue/errors", broadcast = false)
	public Map<String, Object> handleUnknown(Exception e, SimpMessageHeaderAccessor accessor) {
		log.error("WebSocket 메시지 처리 오류", e);
		return error("INTERNAL_ERROR", "메시지를 보내지 못했습니다. 잠시 후 다시 시도해 주세요.", accessor);
	}

	private Map<String, Object> error(String code, String message, SimpMessageHeaderAccessor accessor) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("code", code);
		body.put("message", message);
		body.put("roomId", roomIdOf(accessor.getDestination()));
		return body;
	}

	/** /app/chat/{roomId} 에서 roomId 꺼내기 (어느 방에서 실패했는지 화면에 표시용) */
	private Long roomIdOf(String destination) {
		if (destination == null) {
			return null;
		}
		try {
			return Long.valueOf(destination.substring(destination.lastIndexOf('/') + 1));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
