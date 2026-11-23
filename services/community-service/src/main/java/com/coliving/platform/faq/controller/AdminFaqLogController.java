package com.coliving.platform.faq.controller;

import com.coliving.platform.common.auth.ResidentGuard;
import com.coliving.platform.faq.dto.FaqQuestionLogListResponseDTO;
import com.coliving.platform.faq.service.AdminFaqLogService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 7-8 FAQ 봇 질문 기록 — 관리자
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/admin/faq-bot/logs")
@RequiredArgsConstructor
public class AdminFaqLogController {

	private final AdminFaqLogService adminFaqLogService;
	private final ResidentGuard residentGuard;

	/**
	 * FAQ 봇 질문 기록을 조회한다.
	 * 관리자가 답변 품질과 미응답 질문을 확인할 때 사용한다. 날짜·건물·응답 여부로 검색한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param answered 근거 문서로 답변했는지 여부 (예: true), 생략 가능
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @param from 검색 시작일 (예: 2026-11-01, 포함)
	 * @param to 검색 종료일 (예: 2026-11-12, 포함)
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20
	 * @return 200 질문 기록 페이지와 전체 집계
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public FaqQuestionLogListResponseDTO getLogs(@RequestHeader("X-User") Long userId,
			@RequestParam(required = false) Boolean answered, @RequestParam(required = false) Long buildingId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		residentGuard.requireAdmin(userId);
		return adminFaqLogService.getLogs(answered, buildingId, from, to, page, size);
	}
}
