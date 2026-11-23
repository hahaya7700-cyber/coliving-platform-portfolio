package com.coliving.platform.notification.event;

import com.coliving.platform.common.entity.SenderType;
import com.coliving.platform.notification.dto.NotificationItemDTO;

/**
 * 알림 저장 완료 → 커밋 후 실시간 전송 (NotificationPushListener)
 *
 * @param receiverType 수신자 유형 (USER / ADMIN 중 하나)
 * @param receiverId 수신자 ID (0은 관리자 공용)
 * @param notification 실시간 전송할 알림 항목
 * @author 오시문
 */
public record NotificationCreatedEvent(SenderType receiverType, Long receiverId, NotificationItemDTO notification) {
}
