package com.coliving.platform.safety.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 관리자 제재 해제 API 요청.
 * 9-5 제재 해제
 *
 * @param revokeReason 제재 해제 사유 (예: 이의 신청 수용), 필수, 1~500자
 * @author 오시문
 */
public record SanctionRevokeRequestDTO(
		@NotBlank(message = "해제 사유를 입력해 주세요.")
		@Size(max = 500, message = "해제 사유는 500자 이하로 입력해 주세요.")
		String revokeReason
) {
}
