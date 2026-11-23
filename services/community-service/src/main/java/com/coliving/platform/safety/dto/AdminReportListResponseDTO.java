package com.coliving.platform.safety.dto;

import java.util.List;

/**
 * 관리자 신고 목록 조회 API의 페이지 응답.
 * 9-1 신고 목록 응답
 *
 * @param summary 목록 전체 집계 정보
 * @param content 현재 페이지의 항목 목록
 * @param page 0부터 시작하는 페이지 번호 (예: 0)
 * @param size 한 페이지 항목 수 (예: 20)
 * @param totalElements 조회 조건에 맞는 전체 항목 수 (예: 25)
 * @param totalPages 전체 페이지 수 (예: 2)
 * @author 오시문
 */
public record AdminReportListResponseDTO(
		ReportSummaryDTO summary,
		List<AdminReportItemDTO> content,
		int page,
		int size,
		long totalElements,
		int totalPages
) {
}
