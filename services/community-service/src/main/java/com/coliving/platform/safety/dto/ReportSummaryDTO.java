package com.coliving.platform.safety.dto;

/**
 * 관리자 신고 목록 조회 API의 상태별 대상 수 응답.
 * 9-1 상태별 신고 대상 수 (receivedCount = 사이드바 배지)
 *
 * @param receivedCount 접수 상태인 신고 대상 수 (관리자 배지)
 * @param resolvedCount 처리 완료 신고 대상 수 (예: 2)
 * @param rejectedCount 기각된 신고 대상 수 (예: 1)
 * @author 오시문
 */
public record ReportSummaryDTO(
		long receivedCount,
		long resolvedCount,
		long rejectedCount
) {
}
