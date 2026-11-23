package com.coliving.platform.chat.dto;

/**
 * 채팅방 나가기 후 /user/queue/chat-events로 보내는 이벤트 응답.
 * 4-5 채팅방 이벤트 (/user/queue/chat-events) — type: PARTNER_LEFT (상대가 나감)
 *
 * @param type 채팅방 이벤트 종류 (PARTNER_LEFT: 상대 나감)
 * @param roomId 채팅방 ID (예: 1)
 * @param userId 회원 ID (예: 5001)
 * @author 오시문
 */
public record ChatRoomEventDTO(String type, Long roomId, Long userId) {

	public static ChatRoomEventDTO partnerLeft(Long roomId, Long userId) {
		return new ChatRoomEventDTO("PARTNER_LEFT", roomId, userId);
	}
}
