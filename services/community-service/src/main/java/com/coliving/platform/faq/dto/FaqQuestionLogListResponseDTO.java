package com.coliving.platform.faq.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 FAQ 질문 기록 조회 API 응답.
 * 7-8 질문 기록 (PageResponse 형식 + summary)
 *
 * @param summary 목록 전체 집계 정보
 * @param content 현재 페이지의 항목 목록
 * @param page 0부터 시작하는 페이지 번호 (예: 0)
 * @param size 한 페이지 항목 수 (예: 20)
 * @param totalElements 조회 조건에 맞는 전체 항목 수 (예: 25)
 * @param totalPages 전체 페이지 수 (예: 2)
 * @author 오시문
 */
public record FaqQuestionLogListResponseDTO(
		Summary summary,
		List<Item> content,
		int page,
		int size,
		long totalElements,
		int totalPages
) {

	/**
	 * 목록 조회 API의 전체 집계 응답.
	 *
	 * @param totalCount 전체 항목 수 (예: 10)
	 * @param unansweredCount 답변하지 못한 질문 수 (예: 2)
	 * @param cachedCount 캐시 응답 수 (예: 3)
	 * @param avgLatencyMs 평균 응답 시간 (밀리초, 예: 1200)
	 * @author 오시문
	 */
	public record Summary(long totalCount, long unansweredCount, long cachedCount, int avgLatencyMs) {
	}

	/**
	 * 목록 조회 API의 개별 응답 항목.
	 *
	 * @param logId FAQ 질문 기록 ID (예: 1)
	 * @param question FAQ 질문 (예: 분리수거는 언제 하나요?)
	 * @param buildingId 건물 ID (예: 1)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @param answered 근거 문서로 답변했는지 여부 (예: true)
	 * @param topScore 검색 결과 최고 유사도 점수 (예: 0.8, 검색 전 반환이면 null)
	 * @param sources 답변의 근거 FAQ 문서 목록
	 * @param cached 캐시에서 반환한 답변인지 여부 (예: false)
	 * @param latencyMs 응답 시간 (밀리초, 예: 1200)
	 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
	 * @author 오시문
	 */
	public record Item(
			Long logId,
			String question,
			Long buildingId,
			String buildingName,
			boolean answered,
			Float topScore,
			List<FaqSourceDTO> sources,
			boolean cached,
			Integer latencyMs,
			LocalDateTime createdAt
	) {
	}
}
