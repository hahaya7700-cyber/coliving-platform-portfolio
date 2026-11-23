package com.coliving.platform.chat.dto;

import com.coliving.platform.chat.document.ChatMessageDocument;
import com.coliving.platform.common.entity.ContentStatus;
import java.time.LocalDateTime;

/**
 * 채팅 메시지 조회·전송 API와 실시간 수신에 쓰는 메시지 응답.
 * 4-3 메시지 · 4-5 실시간 수신 · 4-6 보내기 응답 (숨김·삭제면 content null)
 *
 * @param messageId 메시지 ID (MongoDB ObjectId, 예: 507f1f77bcf86cd799439011)
 * @param senderId 발신 회원 ID (예: 5001)
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param status 현재 처리 상태 (ACTIVE / HIDDEN / DELETED 중 하나)
 * @param sentAt 메시지 전송 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record ChatMessageDTO(
		String messageId,
		Long senderId,
		String content,
		ContentStatus status,
		LocalDateTime sentAt
) {

	public static ChatMessageDTO from(ChatMessageDocument message) {
		return new ChatMessageDTO(message.getId(), message.getSenderId(),
				message.isActive() ? message.getContent() : null, message.getStatus(), message.getSentAt());
	}
}
