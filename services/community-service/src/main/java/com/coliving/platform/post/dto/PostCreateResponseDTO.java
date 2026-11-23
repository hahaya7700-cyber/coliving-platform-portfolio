package com.coliving.platform.post.dto;

import java.time.LocalDateTime;

/**
 * 게시글 작성 API 응답.
 * 5-3 응답
 *
 * @param postId 게시글 ID (예: 1)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record PostCreateResponseDTO(
		Long postId,
		LocalDateTime createdAt
) {
}
