package com.coliving.dashboard.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * 계약 서비스가 보내는 "결제 완료" 이벤트 (활동 로그에 결제 건으로 기록하는 데 사용)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCompletedEvent(
        /* 이벤트 고유 ID */
        String eventId,
        /* 결제 승인 시간 */
        OffsetDateTime occurredAt,
        /* 결제 ID */
        Long paymentId,
        /* 계약 ID */
        Long applicationId,
        /* 계약자 이름 */
        String applicantName,
        /* 건물 ID */
        Long buildingId,
        /* 건물명 */
        String buildingName,
        /* 호실명 */
        String roomName,
        /* 결제 금액 */
        Long amount,
        /* 결제 금액에 포함된 보증금 (옛 메시지에는 없어서 null일 수 있다) */
        Long deposit
) {}