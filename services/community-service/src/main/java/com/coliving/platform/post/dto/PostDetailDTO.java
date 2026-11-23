package com.coliving.platform.post.dto;

import com.coliving.platform.common.entity.SenderType;
import com.coliving.platform.post.entity.PostCategory;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 API 응답.
 * 5-2 게시글 상세
 *
 * @param postId 게시글 ID (예: 1)
 * @param category 게시글 분류 (NOTICE / FREE / SHARE / MEETUP 중 하나)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param authorId 작성 회원 ID (예: 5001)
 * @param authorType 작성자 유형 (USER / ADMIN 중 하나)
 * @param authorName 작성자 표시 이름 (예: 김민서)
 * @param authorRoomNumber 작성자 호실 (예: 302호)
 * @param viewCount 조회수 (예: 10)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @param updatedAt 수정 일시 (예: 2026-11-12T10:00:00)
 * @param isMine 내가 작성한 항목인지 여부 (예: true)
 * @param comments 원댓글과 답글 목록
 * @author 오시문
 */
public record PostDetailDTO(
		Long postId,
		PostCategory category,
		String title,
		String content,
		Long authorId,
		SenderType authorType,
		String authorName,
		String authorRoomNumber,
		int viewCount,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		boolean isMine,
		List<CommentDTO> comments
) {
}
