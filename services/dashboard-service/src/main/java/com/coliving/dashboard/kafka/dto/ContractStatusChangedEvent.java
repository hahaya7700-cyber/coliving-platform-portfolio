package com.coliving.dashboard.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

/**
 * 계약 서비스가 보내는 "계약 상태 변경" 이벤트 (호실 현황과 활동 로그를 함께 갱신하는 데 사용)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContractStatusChangedEvent(
        /* 이벤트 고유 ID */
        String eventId,
        /* 상태가 바뀐 시간 */
        OffsetDateTime occurredAt,
        /* 계약 ID */
        Long applicationId,
        /* 계약자 이름 */
        String applicantName,
        /* 건물 ID */
        Long buildingId,
        /* 건물명 */
        String buildingName,
        /* 호실 ID */
        Long roomId,
        /* 호실명 */
        String roomName,
        /* 변경 전 계약 상태 */
        String previousStatus,
        /* 변경 후 계약 상태 */
        String currentStatus,
        /* 입주일 (yyyy-MM-dd) */
        String moveInDate,
        /* 퇴실 예정일 (yyyy-MM-dd) */
        String moveOutDate,
        /* 보증금 (옛 메시지에는 없어서 null일 수 있다) */
        Long deposit
) {}