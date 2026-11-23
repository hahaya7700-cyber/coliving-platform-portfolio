package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.ReportAction;
import com.coliving.platform.safety.entity.ReportStatus;
import java.time.LocalDateTime;

/**
 * 관리자 신고 처리 API 응답.
 * 9-3 응답
 *
 * @param reportId 신고 ID (예: 1)
 * @param handledReportCount 같은 대상으로 함께 처리한 신고 수 (예: 2)
 * @param status 현재 처리 상태 (RECEIVED / RESOLVED / REJECTED 중 하나)
 * @param action 신고 처리 조치 (REJECT / HIDE_CONTENT / DELETE_CONTENT / WARNING / RESTRICTION / SUSPENSION / OFFLINE_RECORD 중 하나)
 * @param sanctionId 제재 ID (예: 1)
 * @param endsAt 제재 종료 일시 (예: 2026-11-15T10:00:00)
 * @param handledAt 신고 처리 일시 (미처리면 null)
 * @author 오시문
 */
public record ReportActionResponseDTO(
		Long reportId,
		int handledReportCount,
		ReportStatus status,
		ReportAction action,
		Long sanctionId,
		LocalDateTime endsAt,
		LocalDateTime handledAt
) {
}
