package com.coliving.platform.post.dto;

/**
 * 내 커뮤니티 활동 조회 API의 인기글 응답.
 * 5-9 인기글
 *
 * @param postId 게시글 ID (예: 1)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param viewCount 조회수 (예: 10)
 * @param commentCount 댓글 수 (예: 3)
 * @author 오시문
 */
public record PopularPostDTO(
		Long postId,
		String title,
		int viewCount,
		int commentCount
) {
}
