package com.coliving.platform.faq.repository;

/**
 * 7-8 요약 (기간·건물 기준)
 *
 * @param totalCount 전체 항목 수 (예: 10)
 * @param unansweredCount 답변하지 못한 질문 수 (예: 2)
 * @param cachedCount 캐시 응답 수 (예: 3)
 * @param avgLatencyMs 평균 응답 시간 (밀리초, 예: 1200)
 * @author 오시문
 */
public record FaqQuestionLogSummaryRow(Long totalCount, Long unansweredCount, Long cachedCount, Double avgLatencyMs) {
}
