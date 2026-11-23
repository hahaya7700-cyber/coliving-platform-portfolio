package com.coliving.platform.post.dto;

import java.time.LocalDateTime;

/**
 * 내 댓글 수정 API 응답.
 * 5-7 응답
 *
 * @param commentId 댓글 ID (예: 10)
 * @param updatedAt 수정 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record CommentUpdateResponseDTO(
		Long commentId,
		LocalDateTime updatedAt
) {
}
