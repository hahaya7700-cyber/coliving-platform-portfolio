package com.coliving.platform.safety.dto;

import java.time.LocalDateTime;

/**
 * 회원 차단 API 응답.
 * 8-3 응답
 *
 * @param blockedUserId 차단 대상 회원 ID (예: 5002)
 * @param blockedAt 차단 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record BlockResponseDTO(Long blockedUserId, LocalDateTime blockedAt) {
}
