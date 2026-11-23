package com.coliving.platform.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 내 댓글 수정 API 요청.
 * 5-7 댓글 수정
 *
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~1000자
 * @author 오시문
 */
public record CommentUpdateRequestDTO(
		@NotBlank(message = "내용을 입력해 주세요.")
		@Size(max = 1000, message = "1,000자 이하로 입력해 주세요.")
		String content
) {
}
