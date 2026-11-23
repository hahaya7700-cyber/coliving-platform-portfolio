package com.coliving.platform.safety.dto;

import java.time.LocalDateTime;

/**
 * 관리자 제재 해제 API 응답.
 * 9-5 응답
 *
 * @param sanctionId 제재 ID (예: 1)
 * @param revokedAt 제재 해제 일시 (해제하지 않았으면 null)
 * @author 오시문
 */
public record SanctionRevokeResponseDTO(
		Long sanctionId,
		LocalDateTime revokedAt
) {
}
