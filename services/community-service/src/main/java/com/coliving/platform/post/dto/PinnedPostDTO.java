package com.coliving.platform.post.dto;

import com.coliving.platform.common.entity.SenderType;
import com.coliving.platform.post.entity.PostCategory;
import java.time.LocalDateTime;

/**
 * 게시글 목록 조회 API의 상단 고정 공지 응답.
 * 5-1 상단 고정 공지
 *
 * @param postId 게시글 ID (예: 1)
 * @param category 게시글 분류 (NOTICE / FREE / SHARE / MEETUP 중 하나)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param authorName 작성자 표시 이름 (예: 김민서)
 * @param authorType 작성자 유형 (USER / ADMIN 중 하나)
 * @param viewCount 조회수 (예: 10)
 * @param commentCount 댓글 수 (예: 3)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record PinnedPostDTO(
		Long postId,
		PostCategory category,
		String title,
		String authorName,
		SenderType authorType,
		int viewCount,
		int commentCount,
		LocalDateTime createdAt
) {
}
