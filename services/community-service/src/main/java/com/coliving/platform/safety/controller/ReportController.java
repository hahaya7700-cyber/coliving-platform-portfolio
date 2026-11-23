package com.coliving.platform.safety.controller;

import com.coliving.platform.safety.dto.MySanctionDTO;
import com.coliving.platform.safety.dto.ReportCreateRequestDTO;
import com.coliving.platform.safety.dto.ReportCreateResponseDTO;
import com.coliving.platform.safety.service.ReportService;
import com.coliving.platform.safety.service.SanctionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 신고하기 (8-1) · 내 이용 제한 상태 (8-5)
 *
 * @author 오시문
 */
@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;
	private final SanctionQueryService sanctionQueryService;

	/**
	 * 커뮤니티 콘텐츠나 회원을 신고한다.
	 * 입주자가 같은 건물의 게시글·댓글·채팅·회원을 신고할 때 사용한다. 신고만으로 자동 숨김 처리하지 않는다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 신고 위치·대상·사유 (기타이면 상세 사유 필수, 최대 500자)
	 * @return 201 생성된 신고 ID와 접수 상태
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 CANNOT_REPORT_SELF 본인 신고 불가,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         403 OTHER_BUILDING 다른 건물 접근,
	 *         403 SANCTIONED 제재 중,
	 *         404 COMMENT_NOT_FOUND 댓글 없음,
	 *         404 MESSAGE_NOT_FOUND 메시지 없음,
	 *         404 POST_NOT_FOUND 게시글 없음,
	 *         404 ROOM_NOT_FOUND 참여 중인 채팅방 없음,
	 *         404 USER_NOT_FOUND 회원 없음,
	 *         409 ALREADY_REPORTED 이미 신고함,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/community-service/reports")
	public ResponseEntity<ReportCreateResponseDTO> create(
			@RequestHeader("X-User") Long userId,
			@Valid @RequestBody ReportCreateRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(reportService.create(userId, request));
	}

	/**
	 * 내 이용 제한 상태를 조회한다.
	 * 회원 화면이 현재 커뮤니티 이용 제한 여부를 표시할 때 사용한다. 유효한 제재가 없으면 active는 false다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 현재 제재 정보 또는 제재 없음
	 */
	@GetMapping("/community-service/sanctions/me")
	public MySanctionDTO getMySanction(@RequestHeader("X-User") Long userId) {
		return sanctionQueryService.getMySanction(userId);
	}
}
