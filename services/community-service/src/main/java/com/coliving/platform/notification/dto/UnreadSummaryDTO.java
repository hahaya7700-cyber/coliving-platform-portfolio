package com.coliving.platform.notification.dto;

/**
 * 안 읽은 쪽지·채팅·알림 수 조회 API 응답.
 * 6-2 안 읽은 수 (total = note + chat, 위젯 빨간 점 기준)
 *
 * @param note 사용자는 안 읽은 받은 쪽지 수, 관리자는 미답변 쪽지함 수
 * @param chat 안 읽은 채팅 메시지 수 (예: 2)
 * @param notification 안 읽은 알림 수 (예: 3)
 * @param total 위젯 표시용 안 읽은 수 합계 (note + chat)
 * @author 오시문
 */
public record UnreadSummaryDTO(
		long note,
		long chat,
		long notification,
		long total
) {
}
