package com.coliving.platform.chat.dto;

import com.coliving.platform.common.entity.ContentStatus;
import java.time.LocalDateTime;

/**
 * 저장한 채팅 메시지를 /user/queue/chat 구독자에게 보내는 응답.
 * 4-5 실시간 수신 (/user/queue/chat) — 4-3 메시지 항목 + roomId (모든 방 메시지가 한 채널로 옴)
 *
 * @param roomId 채팅방 ID (예: 1)
 * @param messageId 메시지 ID (MongoDB ObjectId, 예: 507f1f77bcf86cd799439011)
 * @param senderId 발신 회원 ID (예: 5001)
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param status 현재 처리 상태 (ACTIVE / HIDDEN / DELETED 중 하나)
 * @param sentAt 메시지 전송 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record ChatMessagePushDTO(
		Long roomId,
		String messageId,
		Long senderId,
		String content,
		ContentStatus status,
		LocalDateTime sentAt
) {

	public static ChatMessagePushDTO of(Long roomId, ChatMessageDTO message) {
		return new ChatMessagePushDTO(roomId, message.messageId(), message.senderId(), message.content(),
				message.status(), message.sentAt());
	}
}
