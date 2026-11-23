package com.coliving.platform.chat.event;

import com.coliving.platform.chat.dto.ChatMessageDTO;

/**
 * 메시지 저장 완료 → 커밋 후 실시간 전송·알림 (ChatMessagePushListener)
 * hidden = 받는 사람이 보낸 사람을 차단한 상태 → 보낸 사람에게만 전송, 알림 없음
 *
 * @param roomId 채팅방 ID (예: 1)
 * @param senderId 발신 회원 ID (예: 5001)
 * @param receiverId 수신자 ID (0은 관리자 공용)
 * @param message 전송할 메시지
 * @param hidden 수신자에게 메시지를 숨기는지 여부 (예: false)
 * @author 오시문
 */
public record ChatMessageSentEvent(Long roomId, Long senderId, Long receiverId, ChatMessageDTO message, boolean hidden) {
}
