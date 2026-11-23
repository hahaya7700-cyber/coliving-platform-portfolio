package com.coliving.platform.common.dto;

import java.util.List;

/**
 * 내 커뮤니티 권한 조회 API 응답.
 * 5-11 내 커뮤니티 권한 — 화면이 커뮤니티 메뉴·게시판·채팅을 보여줄지 정할 때 사용
 * resident: 게시판·채팅 사용 가능 / manager: 매니저(관리자)로 게시판 사용 / known: 커뮤니티 서버가 아는 회원인지
 *
 * @param userId 회원 ID (예: 5001)
 * @param known 회원 서비스에서 조회된 회원인지 여부 (예: true)
 * @param name 이름 (예: 김민서)
 * @param roles 회원 역할 목록 (USER / CANDIDATE / RESIDENT / ADMIN)
 * @param resident 실제 입주 중이며 계약상 호실이 확인되는지 여부 (예: true)
 * @param manager 관리자 권한 여부 (예: false)
 * @param buildingId 건물 ID (예: 1)
 * @param buildingName 건물 이름 (예: 강남점)
 * @param roomNumber 호실 (예: 302호)
 * @author 오시문
 */
public record CommunityMeDTO(
		Long userId,
		boolean known,
		String name,
		List<String> roles,
		boolean resident,
		boolean manager,
		Long buildingId,
		String buildingName,
		String roomNumber
) {
}
