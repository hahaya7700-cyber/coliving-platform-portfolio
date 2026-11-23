package com.coliving.dashboard.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * 공용 자원 서비스가 보내는 "예약 상태 변경" 이벤트 (활동 로그에 예약 건으로 기록하는 데 사용)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationStatusChangedEvent(
        /* 이벤트 고유 ID */
        String eventId,
        /* 예약 상태가 바뀐 시간 */
        OffsetDateTime occurredAt,
        /* 예약 ID */
        Long reservationId,
        /* 건물 ID */
        Long buildingId,
        /* 건물명 */
        String buildingName,
        /* 공용 자원 이름 (예: 공용 주방) */
        String resourceName,
        /* 예약자 ID */
        Long userId,
        /* PENDING(대기) / APPROVED(승인) / REJECTED(반려) / CANCELED(취소) */
        String reservationStatus
) {}