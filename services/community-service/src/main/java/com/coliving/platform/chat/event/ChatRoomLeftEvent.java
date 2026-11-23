package com.coliving.platform.chat.event;

/**
 * 채팅방 나가기 완료 → 커밋 후 남은 사람에게 알림 (partnerId 가 null 이면 이미 둘 다 나감)
 *
 * @param roomId 채팅방 ID (예: 1)
 * @param leftUserId 채팅방을 나간 회원 ID (예: 5002)
 * @param partnerId 채팅 상대 회원 ID (예: 5002)
 * @author 오시문
 */
public record ChatRoomLeftEvent(Long roomId, Long leftUserId, Long partnerId) {
}
