package com.coliving.platform.post.dto;

import java.time.LocalDateTime;

/**
 * 게시글 수정 API 응답.
 * 5-4 응답
 *
 * @param postId 게시글 ID (예: 1)
 * @param updatedAt 수정 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record PostUpdateResponseDTO(
		Long postId,
		LocalDateTime updatedAt
) {
}
