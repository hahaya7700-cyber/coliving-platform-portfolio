package com.coliving.platform.post.dto;

import com.coliving.platform.safety.dto.MySanctionDTO;
import java.util.List;

/**
 * 내 커뮤니티 활동·인기글 조회 API 응답.
 * 5-9 내 커뮤니티 정보 · 인기글
 *
 * @param name 이름 (예: 김민서)
 * @param buildingName 건물 이름 (예: 강남점)
 * @param roomNumber 호실 (예: 302호)
 * @param postCount 내 활성 게시글 수 (예: 4)
 * @param receivedCommentCount 내 게시글에 받은 활성 댓글 수 (예: 3)
 * @param sanction 진행 중인 이용 제한·정지 (없으면 null)
 * @param popularPosts 최근 7일의 조회수 기준 인기글 최대 3개
 * @author 오시문
 */
public record MySummaryDTO(
		String name,
		String buildingName,
		String roomNumber,
		long postCount,
		long receivedCommentCount,
		/** 진행 중인 이용 제한·정지 (없으면 null) */
		MySanctionDTO sanction,
		List<PopularPostDTO> popularPosts
) {
}
