package com.coliving.platform.note.controller;

import com.coliving.platform.note.dto.GuestInquiryRequestDTO;
import com.coliving.platform.note.dto.GuestInquiryResponseDTO;
import com.coliving.platform.note.service.GuestInquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 비회원 문의 (API 2-1) — 로그인 불필요 (Gateway에서 인증 제외 경로로 설정 필요)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/guest-inquiries")
@RequiredArgsConstructor
public class GuestInquiryController {

	private final GuestInquiryService guestInquiryService;

	/**
	 * 비회원 문의를 접수한다.
	 * 로그인하지 않은 방문자가 문의할 때 사용한다. 개인정보 동의가 필요하며 같은 전화번호의 재제출은 10분간 제한한다.
	 *
	 * @param request 이름·전화번호·이메일·문의 (1~1000자)와 개인정보 동의
	 * @return 201 접수된 문의 ID와 생성 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         429 TOO_MANY_REQUESTS 호출 제한 초과
	 */
	@PostMapping
	public ResponseEntity<GuestInquiryResponseDTO> create(@Valid @RequestBody GuestInquiryRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(guestInquiryService.create(request));
	}
}
