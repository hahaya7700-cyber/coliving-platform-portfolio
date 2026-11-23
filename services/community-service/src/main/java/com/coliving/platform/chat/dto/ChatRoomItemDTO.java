package com.coliving.platform.chat.dto;

import java.time.LocalDateTime;

/**
 * 내 채팅방 목록 조회 API의 채팅방 응답 항목.
 * 4-1 채팅방 목록 항목 (partnerLeft = 상대가 나간 방 → readOnly, "상대방이 대화방을 나갔습니다" 표시)
 *
 * @param roomId 채팅방 ID (예: 1)
 * @param partner 채팅 상대 표시 정보
 * @param lastMessage 최근 메시지 미리보기 (숨김·삭제 메시지는 null)
 * @param lastMessageAt 최근 메시지 일시 (메시지가 없으면 null)
 * @param unreadCount 안 읽은 항목 수 (예: 3)
 * @param readOnly 메시지 전송이 제한된 읽기 전용 방인지 여부 (예: false)
 * @param partnerLeft 상대가 채팅방을 나갔는지 여부 (예: false)
 * @author 오시문
 */
public record ChatRoomItemDTO(
		Long roomId,
		Partner partner,
		String lastMessage,
		LocalDateTime lastMessageAt,
		long unreadCount,
		boolean readOnly,
		boolean partnerLeft
) {

	/**
	 * 채팅방 목록 응답에 포함되는 상대 회원 표시 정보.
	 *
	 * @param userId 회원 ID (예: 5001)
	 * @param name 이름 (예: 김민서)
	 * @param profileImageUrl 프로필 이미지 URL (예: https://example.com/profile.png, 없으면 null)
	 * @param roomNumber 호실 (예: 302호)
	 * @author 오시문
	 */
	public record Partner(Long userId, String name, String profileImageUrl, String roomNumber) {
	}
}
