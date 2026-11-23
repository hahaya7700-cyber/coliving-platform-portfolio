package com.coliving.dashboard.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * 계약 서비스가 보내는 "환불 완료" 이벤트 (활동 로그와 당월 환불 집계에 사용)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentRefundedEvent(
        /* 이벤트 고유 ID */
        String eventId,
        /* 환불 완료 시간 (payment.canceled_at, KST) */
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
        /* 환불 금액 (입주 전 취소는 전액) */
        Long refundAmount,
        /* 환불 금액에 포함된 보증금 (옛 메시지에는 없어서 null일 수 있다) */
        Long deposit
) {}