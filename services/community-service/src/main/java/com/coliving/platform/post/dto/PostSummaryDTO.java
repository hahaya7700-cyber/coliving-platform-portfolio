package com.coliving.platform.post.dto;

import com.coliving.platform.common.entity.SenderType;
import com.coliving.platform.post.entity.PostCategory;
import java.time.LocalDateTime;

/**
 * 게시글 목록 조회 API의 응답 항목.
 * 5-1 게시글 목록 항목
 *
 * @param postId 게시글 ID (예: 1)
 * @param category 게시글 분류 (NOTICE / FREE / SHARE / MEETUP 중 하나)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param authorId 작성 회원 ID (예: 5001)
 * @param authorType 작성자 유형 (USER / ADMIN 중 하나)
 * @param authorName 작성자 표시 이름 (예: 김민서)
 * @param authorRoomNumber 작성자 호실 (예: 302호)
 * @param viewCount 조회수 (예: 10)
 * @param commentCount 댓글 수 (예: 3)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @param isNew 최근 24시간 안에 작성된 글인지 여부 (예: true)
 * @author 오시문
 */
public record PostSummaryDTO(
		Long postId,
		PostCategory category,
		String title,
		Long authorId,
		SenderType authorType,
		String authorName,
		String authorRoomNumber,
		int viewCount,
		int commentCount,
		LocalDateTime createdAt,
		boolean isNew
) {
}
