package com.coliving.platform.chat.controller;

import com.coliving.platform.chat.dto.ChatMessageDTO;
import com.coliving.platform.chat.dto.ChatMessageListResponseDTO;
import com.coliving.platform.chat.dto.ChatMessageSendRequestDTO;
import com.coliving.platform.chat.dto.ChatReadRequestDTO;
import com.coliving.platform.chat.dto.ChatRoomItemDTO;
import com.coliving.platform.chat.dto.ChatRoomOpenRequestDTO;
import com.coliving.platform.chat.dto.ChatRoomOpenResponseDTO;
import com.coliving.platform.chat.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 1:1 채팅 REST (API 4-1 ~ 4-4, 4-6, 4-7) — 실시간은 WebSocket(4-5)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/chat/rooms")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	/**
	 * 내 채팅방 목록을 조회한다.
	 * 회원이 채팅 목록을 열 때 사용한다. 나간 방과 내가 차단한 상대의 방은 제외한다.
	 * 4-1 채팅방 목록
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 채팅방 목록
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public List<ChatRoomItemDTO> getRooms(@RequestHeader("X-User") Long userId) {
		return chatService.getRooms(userId);
	}

	/**
	 * 상대 회원과의 채팅방을 연다.
	 * 같은 건물의 입주자가 대화를 시작할 때 사용한다. 진행 중인 방이 있으면 재사용한다.
	 * 4-2 채팅방 열기 — 기존 방 200, 새 방 201
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 상대 회원 ID와 채팅 시작 위치 (POST이면 게시글 ID 필수)
	 * @return 200 기존 채팅방 또는 201 새 채팅방 ID
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 CHAT_NOT_AVAILABLE 채팅 불가,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         403 OTHER_BUILDING 다른 건물 접근,
	 *         403 SANCTIONED 제재 중,
	 *         404 POST_NOT_FOUND 게시글 없음,
	 *         404 USER_NOT_FOUND 회원 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping
	public ResponseEntity<ChatRoomOpenResponseDTO> openRoom(@RequestHeader("X-User") Long userId,
			@Valid @RequestBody ChatRoomOpenRequestDTO request) {
		ChatRoomOpenResponseDTO response = chatService.openRoom(userId, request);
		return ResponseEntity.status(response.created() ? HttpStatus.CREATED : HttpStatus.OK).body(response);
	}

	/**
	 * 채팅방의 메시지 목록을 조회한다.
	 * 참여 중인 회원이 대화 내역을 불러올 때 사용한다. 숨긴 메시지를 제외하고 오래된 순서로 반환한다.
	 * 4-3 메시지 목록
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param roomId 채팅방 ID (예: 1)
	 * @param before 이 메시지 ID 이전부터 조회 (MongoDB ObjectId, 생략 시 최신)
	 * @param size 한 페이지 항목 수 (예: 20), 기본 30
	 * @return 200 메시지 목록과 이전 메시지 존재 여부
	 * @throws com.coliving.platform.common.exception.CommunityException 404 MESSAGE_NOT_FOUND 메시지 없음,
	 *         404 ROOM_NOT_FOUND 참여 중인 채팅방 없음
	 */
	@GetMapping("/{roomId}/messages")
	public ChatMessageListResponseDTO getMessages(@RequestHeader("X-User") Long userId, @PathVariable Long roomId,
			@RequestParam(required = false) String before, @RequestParam(defaultValue = "30") int size) {
		return chatService.getMessages(userId, roomId, before, size);
	}

	/**
	 * 채팅 메시지를 읽음 처리한다.
	 * 참여 중인 회원이 대화를 읽었을 때 사용한다. 해당 방의 보이는 메시지만 읽음 위치로 지정할 수 있다.
	 * 4-4 읽음 처리
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param roomId 채팅방 ID (예: 1)
	 * @param request 이 방에서 마지막으로 읽은 메시지 ID
	 * @return 204 읽음 처리 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 404 MESSAGE_NOT_FOUND 메시지 없음,
	 *         404 ROOM_NOT_FOUND 참여 중인 채팅방 없음
	 */
	@PatchMapping("/{roomId}/read")
	public ResponseEntity<Void> markRead(@RequestHeader("X-User") Long userId, @PathVariable Long roomId,
			@Valid @RequestBody ChatReadRequestDTO request) {
		chatService.markRead(userId, roomId, request.lastReadMessageId());
		return ResponseEntity.noContent().build();
	}

	/**
	 * 채팅방에서 나간다.
	 * 참여 중인 회원이 대화를 종료할 때 사용한다. 방은 읽기 전용이 되고 다시 참여할 수 없다.
	 * 4-7 채팅방 나가기
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param roomId 채팅방 ID (예: 1)
	 * @return 204 나가기 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 404 ROOM_NOT_FOUND 참여 중인 채팅방 없음
	 */
	@PostMapping("/{roomId}/leave")
	public ResponseEntity<Void> leaveRoom(@RequestHeader("X-User") Long userId, @PathVariable Long roomId) {
		chatService.leaveRoom(userId, roomId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 채팅 메시지를 보낸다.
	 * WebSocket 연결이 끊긴 화면에서 사용하는 REST 경로다. 저장 후 /user/queue/chat으로 전달하며 상대가 나를 차단했으면 발신자에게만 전송한다.
	 * 4-6 메시지 보내기 (REST) — WebSocket 연결이 안 될 때 대신 사용, 테스트용
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param roomId 채팅방 ID (예: 1)
	 * @param request 보낼 메시지 내용 (공백만 입력 불가, 1~1000자)
	 * @return 201 저장된 메시지
	 * @throws com.coliving.platform.common.exception.CommunityException 403 CHAT_NOT_AVAILABLE 채팅 불가,
	 *         403 CHAT_READ_ONLY 읽기 전용 방,
	 *         403 SANCTIONED 제재 중,
	 *         404 ROOM_NOT_FOUND 참여 중인 채팅방 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/{roomId}/messages")
	public ResponseEntity<ChatMessageDTO> sendMessage(@RequestHeader("X-User") Long userId, @PathVariable Long roomId,
			@Valid @RequestBody ChatMessageSendRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(userId, roomId, request.content()));
	}
}
