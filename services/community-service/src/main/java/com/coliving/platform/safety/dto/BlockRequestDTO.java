package com.coliving.platform.safety.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 회원 차단 API 요청.
 * 8-3 차단하기
 *
 * @param blockedUserId 차단 대상 회원 ID (예: 5002), 필수
 * @author 오시문
 */
public record BlockRequestDTO(
		@NotNull(message = "차단할 사용자를 선택해 주세요.")
		Long blockedUserId
) {
}
