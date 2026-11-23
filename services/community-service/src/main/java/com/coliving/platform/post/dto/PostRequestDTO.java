package com.coliving.platform.post.dto;

import com.coliving.platform.post.entity.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 게시글 작성·수정 API 요청.
 * 5-3 게시글 작성 · 5-4 수정
 *
 * @param category 게시글 분류 (NOTICE / FREE / SHARE / MEETUP 중 하나), 필수
 * @param title 제목 (예: 공용 공간 이용 안내), 필수, 1~100자
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~5000자
 * @param buildingId 건물 ID (예: 1)
 * @author 오시문
 */
public record PostRequestDTO(
		@NotNull(message = "카테고리를 선택해 주세요.")
		PostCategory category,

		@NotBlank(message = "제목을 입력해 주세요.")
		@Size(max = 100, message = "제목은 100자 이하로 입력해 주세요.")
		String title,

		@NotBlank(message = "내용을 입력해 주세요.")
		@Size(max = 5000, message = "내용은 5,000자 이하로 입력해 주세요.")
		String content,

		/** 관리자 공지 작성 시에만 필수 (입주자는 무시하고 입주 건물로 저장) */
		Long buildingId
) {
}
