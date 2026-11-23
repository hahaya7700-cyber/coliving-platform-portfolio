package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.ReportStatus;
import java.time.LocalDateTime;

/**
 * 신고 접수 API 응답.
 * 8-1 응답
 *
 * @param reportId 신고 ID (예: 1)
 * @param status 현재 처리 상태 (RECEIVED / RESOLVED / REJECTED 중 하나)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record ReportCreateResponseDTO(
		Long reportId,
		ReportStatus status,
		LocalDateTime createdAt
) {
}
