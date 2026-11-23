package com.coliving.platform.post.dto;

import java.time.LocalDateTime;

/**
 * 게시글 댓글·답글 작성 API 응답.
 * 5-6 응답
 *
 * @param commentId 댓글 ID (예: 10)
 * @param parentCommentId 원댓글 ID (예: 10, 원댓글 작성 시 null, 답글은 1단계만 허용)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record CommentCreateResponseDTO(
		Long commentId,
		Long parentCommentId,
		LocalDateTime createdAt
) {
}
