package com.coliving.platform.safety.controller;

import com.coliving.platform.common.auth.ResidentGuard;
import com.coliving.platform.common.dto.PageResponse;
import com.coliving.platform.safety.dto.AdminReportDetailDTO;
import com.coliving.platform.safety.dto.AdminReportListResponseDTO;
import com.coliving.platform.safety.dto.AdminSanctionItemDTO;
import com.coliving.platform.safety.dto.ReportActionRequestDTO;
import com.coliving.platform.safety.dto.ReportActionResponseDTO;
import com.coliving.platform.safety.dto.SanctionRevokeRequestDTO;
import com.coliving.platform.safety.dto.SanctionRevokeResponseDTO;
import com.coliving.platform.safety.entity.ReportSourceType;
import com.coliving.platform.safety.entity.ReportStatus;
import com.coliving.platform.safety.entity.ReportTargetType;
import com.coliving.platform.safety.entity.SanctionType;
import com.coliving.platform.safety.service.AdminReportService;
import com.coliving.platform.safety.service.AdminSanctionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 신고 · 제재 관리 — 관리자 (API 9-1 ~ 9-5)
 * 관리자 확인: 지금은 유저 정보의 role = ADMIN (TODO: Gateway 역할 헤더로 교체)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/admin")
@RequiredArgsConstructor
public class AdminReportController {

	private final AdminReportService adminReportService;
	private final AdminSanctionService adminSanctionService;
	private final ResidentGuard residentGuard;

	/**
	 * 신고 대상 목록을 조회한다.
	 * 관리자가 접수된 신고를 검토할 때 사용한다. 같은 대상의 신고를 묶고 상태별 집계를 함께 반환한다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param status 현재 처리 상태 (RECEIVED / RESOLVED / REJECTED 중 하나), 생략 가능
	 * @param sourceType 작성을 시작한 위치 유형 (CHAT_ROOM / POST 중 하나), 생략 가능
	 * @param targetType 대상 종류 (POST / COMMENT / CHAT_MESSAGE / USER 중 하나), 생략 가능
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @param sort 정렬 기준 (latest / count), 기본 latest
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20, 1 이상, 100 이하
	 * @return 200 신고 대상 페이지와 상태별 집계
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/reports")
	public AdminReportListResponseDTO getReports(
			@RequestHeader("X-User") Long adminId,
			@RequestParam(required = false) ReportStatus status,
			@RequestParam(required = false) ReportSourceType sourceType,
			@RequestParam(required = false) ReportTargetType targetType,
			@RequestParam(required = false) Long buildingId,
			@RequestParam(defaultValue = "latest") String sort,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		residentGuard.requireAdmin(adminId);
		return adminReportService.getReports(status, sourceType, targetType, buildingId, sort, page, size);
	}

	/**
	 * 신고 대상의 상세와 이력을 조회한다.
	 * 관리자가 신고를 처리하기 전 대상 내용과 제재 이력을 확인할 때 사용한다. 신고자는 익명으로 제공한다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param reportId 신고 ID (예: 1)
	 * @return 200 대상 내용·신고 내역·제재 이력
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         404 REPORT_NOT_FOUND 신고 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/reports/{reportId}")
	public AdminReportDetailDTO getReport(@RequestHeader("X-User") Long adminId, @PathVariable Long reportId) {
		residentGuard.requireAdmin(adminId);
		return adminReportService.getReport(reportId);
	}

	/**
	 * 신고에 대한 관리자 조치를 처리한다.
	 * 관리자가 경고·이용 제한·정지 또는 기각을 결정할 때 사용한다. 같은 대상의 미처리 신고를 함께 처리한다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param reportId 신고 ID (예: 1)
	 * @param request 신고 조치·제한 기간·숨김 여부·사유와 관리자 메모
	 * @return 200 처리 결과·제재 ID와 종료 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_DURATION 제한 기간 오류,
	 *         400 INVALID_REQUEST 요청 값 오류,
	 *         403 ADMIN_ONLY 관리자 전용,
	 *         404 REPORT_NOT_FOUND 신고 없음,
	 *         409 ALREADY_HANDLED 이미 처리함,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/reports/{reportId}/actions")
	public ReportActionResponseDTO handle(
			@RequestHeader("X-User") Long adminId,
			@PathVariable Long reportId,
			@Valid @RequestBody ReportActionRequestDTO request) {
		residentGuard.requireAdmin(adminId);
		return adminReportService.handle(adminId, reportId, request);
	}

	/**
	 * 회원 제재 목록을 조회한다.
	 * 관리자가 유효한 제재와 과거 이력을 검색할 때 사용한다. 종류·건물·검색어로 범위를 좁힌다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param activeOnly 현재 유효한 제재만 조회할지 여부 (기본 true), 기본 true
	 * @param sanctionType 제재 유형 (WARNING / RESTRICTION / SUSPENSION 중 하나), 생략 가능
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @param keyword 검색어 (예: 공용 공간)
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20, 1 이상, 100 이하
	 * @return 200 제재 목록 페이지
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/sanctions")
	public PageResponse<AdminSanctionItemDTO> getSanctions(
			@RequestHeader("X-User") Long adminId,
			@RequestParam(defaultValue = "true") boolean activeOnly,
			@RequestParam(required = false) SanctionType sanctionType,
			@RequestParam(required = false) Long buildingId,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		residentGuard.requireAdmin(adminId);
		return adminSanctionService.getSanctions(activeOnly, sanctionType, buildingId, keyword, page, size);
	}

	/**
	 * 회원 제재를 해제한다.
	 * 관리자가 진행 중인 제재를 취소할 때 사용한다. 이미 해제되거나 만료된 제재는 해제할 수 없다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param sanctionId 제재 ID (예: 1)
	 * @param request 제재 해제 사유 (공백만 입력 불가, 1~500자)
	 * @return 200 해제된 제재 ID와 해제 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         404 SANCTION_NOT_FOUND 제재 없음,
	 *         409 SANCTION_ALREADY_REVOKED 이미 해제·만료됨,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PatchMapping("/sanctions/{sanctionId}/revoke")
	public SanctionRevokeResponseDTO revoke(
			@RequestHeader("X-User") Long adminId,
			@PathVariable Long sanctionId,
			@Valid @RequestBody SanctionRevokeRequestDTO request) {
		residentGuard.requireAdmin(adminId);
		return adminSanctionService.revoke(adminId, sanctionId, request.revokeReason());
	}
}
