package com.coliving.platform.post.dto;

import java.util.List;

/**
 * 게시글 목록 조회 API의 페이지 응답.
 * 5-1 게시글 목록 응답
 *
 * @param buildingId 건물 ID (예: 1)
 * @param buildingName 건물 이름 (예: 강남점)
 * @param pinned 상단 고정 공지 목록
 * @param content 현재 페이지의 항목 목록
 * @param page 0부터 시작하는 페이지 번호 (예: 0)
 * @param size 한 페이지 항목 수 (예: 20)
 * @param totalElements 조회 조건에 맞는 전체 항목 수 (예: 25)
 * @param totalPages 전체 페이지 수 (예: 2)
 * @author 오시문
 */
public record PostListResponseDTO(
		Long buildingId,
		String buildingName,
		List<PinnedPostDTO> pinned,
		List<PostSummaryDTO> content,
		int page,
		int size,
		long totalElements,
		int totalPages
) {
}
