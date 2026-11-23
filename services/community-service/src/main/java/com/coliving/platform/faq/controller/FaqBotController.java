package com.coliving.platform.faq.controller;

import com.coliving.platform.faq.dto.FaqAskRequestDTO;
import com.coliving.platform.faq.dto.FaqAskResponseDTO;
import com.coliving.platform.faq.service.FaqBotService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 7-1 FAQ 봇 질문 — 비회원 포함 누구나 (X-User 없으면 비회원)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/faq-bot")
@RequiredArgsConstructor
public class FaqBotController {

	private final FaqBotService faqBotService;

	/**
	 * FAQ 봇에 질문한다.
	 * 비회원·회원·관리자가 이용 규정을 물을 때 사용한다. 근거가 없거나 AI 호출이 어려우면 안내 답변을 반환한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID (비회원이면 생략)
	 * @param request 질문 (1~300자)과 선택한 건물 ID
	 * @param httpRequest 비회원 호출 제한용 클라이언트 IP를 확인할 HTTP 요청
	 * @return 200 답변·근거 문서·후속 행동과 캐시 여부
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         429 TOO_MANY_REQUESTS 호출 제한 초과,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/ask")
	public FaqAskResponseDTO ask(@RequestHeader(value = "X-User", required = false) Long userId,
			@Valid @RequestBody FaqAskRequestDTO request, HttpServletRequest httpRequest) {
		return faqBotService.ask(userId, clientIp(httpRequest), request.question(), request.buildingId());
	}

	/** 비회원 횟수 제한용 IP — Gateway 를 거치면 X-Forwarded-For 첫 번째 값 */
	private String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].strip();
		}
		return request.getRemoteAddr();
	}
}
