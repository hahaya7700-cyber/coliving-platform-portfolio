package com.coliving.platform.post.controller;

import com.coliving.platform.post.dto.MySummaryDTO;
import com.coliving.platform.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 내 커뮤니티 정보 · 인기글 (API 5-9)
 *
 * @author 오시문
 */
@RestController
@RequiredArgsConstructor
public class MySummaryController {

	private final PostService postService;

	/**
	 * 내 커뮤니티 활동과 인기글을 조회한다.
	 * 입주자가 내 활동 요약을 열 때 사용한다. 현재 제재와 최근 7일의 인기글 최대 3개를 함께 반환한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 내 활동 집계·제재와 인기글
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_RESIDENT 입주자 아님,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/community-service/me/summary")
	public MySummaryDTO getMySummary(@RequestHeader("X-User") Long userId) {
		return postService.getMySummary(userId);
	}
}
