package com.coliving.platform.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 게시글 댓글·답글 작성 API 요청.
 * 5-6 댓글 · 답글 작성
 *
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~1000자
 * @param parentCommentId 원댓글 ID (예: 10, 원댓글 작성 시 null, 답글은 1단계만 허용)
 * @author 오시문
 */
public record CommentRequestDTO(
		@NotBlank(message = "내용을 입력해 주세요.")
		@Size(max = 1000, message = "1,000자 이하로 입력해 주세요.")
		String content,

		Long parentCommentId
) {
}
