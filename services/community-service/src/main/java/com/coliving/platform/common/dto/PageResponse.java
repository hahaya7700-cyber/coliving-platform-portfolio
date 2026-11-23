package com.coliving.platform.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * 쪽지함·알림·제재 목록 API에서 공통으로 사용하는 페이지 응답.
 * 목록 응답 공통 형식 (API 명세서 0-2)
 *
 * @param <T> 페이지 응답 항목의 자료형
 * @param content 현재 페이지의 항목 목록
 * @param page 0부터 시작하는 페이지 번호 (예: 0)
 * @param size 한 페이지 항목 수 (예: 20)
 * @param totalElements 조회 조건에 맞는 전체 항목 수 (예: 25)
 * @param totalPages 전체 페이지 수 (예: 2)
 * @author 오시문
 */
public record PageResponse<T>(
		List<T> content,
		int page,
		int size,
		long totalElements,
		int totalPages
) {

	public static <T> PageResponse<T> of(Page<?> page, List<T> content) {
		return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
	}
}
