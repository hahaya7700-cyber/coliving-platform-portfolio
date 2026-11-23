package com.coliving.platform.post.controller;

import com.coliving.platform.client.PropertyClient;
import com.coliving.platform.common.auth.ResidentGuard;
import com.coliving.platform.post.dto.BoardBuildingDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 매니저가 사용자 게시판(user-web)에서 볼 건물 선택 목록
 * TODO(연동 1-3): 관리자 담당 건물만 (지금은 전체 건물)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/admin/buildings")
@RequiredArgsConstructor
public class AdminBoardBuildingController {

	private final ResidentGuard residentGuard;
	private final PropertyClient propertyClient;

	/**
	 * 관리자 게시판의 건물 선택 목록을 조회한다.
	 * 관리자가 사용자 게시판에서 건물을 선택할 때 사용한다. 현재 전체 건물 목록을 제공한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 건물 ID와 표시 이름 목록
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public List<BoardBuildingDTO> getBuildings(@RequestHeader("X-User") Long userId) {
		residentGuard.requireAdmin(userId);
		return propertyClient.getBuildings().stream()
				.map(building -> new BoardBuildingDTO(building.buildingId(), building.name()))
				.toList();
	}
}
