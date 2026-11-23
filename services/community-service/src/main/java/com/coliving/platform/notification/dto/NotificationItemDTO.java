package com.coliving.platform.notification.dto;

import com.coliving.platform.notification.entity.NotificationTarget;
import com.coliving.platform.notification.entity.NotificationType;
import java.time.LocalDateTime;

/**
 * 알림 목록 조회·실시간 알림의 응답 항목.
 * 6-1 알림 항목
 *
 * @param notificationId 알림 ID (예: 1)
 * @param type 항목 유형 (NOTE / CHAT / COMMENT / REPLY / SANCTION / REPORT 중 하나)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param message 알림 내용 (예: 새 댓글이 등록됐어요)
 * @param targetType 대상 종류 (NOTE_THREAD / CHAT_ROOM / POST / SANCTION / REPORT 중 하나)
 * @param targetId 대상 ID (채팅 메시지는 MongoDB ObjectId, 나머지는 숫자 ID 문자열)
 * @param read 알림 읽음 여부 (예: false)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record NotificationItemDTO(
		Long notificationId,
		NotificationType type,
		String title,
		String message,
		NotificationTarget targetType,
		String targetId,
		boolean read,
		LocalDateTime createdAt
) {
}
