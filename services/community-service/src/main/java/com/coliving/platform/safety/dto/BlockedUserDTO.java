package com.coliving.platform.safety.dto;

import java.time.LocalDateTime;

/**
 * 내 차단 목록 조회 API의 회원 응답 항목.
 * 8-2 차단 목록 항목
 *
 * @param blockedUserId 차단 대상 회원 ID (예: 5002)
 * @param name 이름 (예: 김민서)
 * @param roomNumber 호실 (예: 302호)
 * @param profileImageUrl 프로필 이미지 URL (예: https://example.com/profile.png, 없으면 null)
 * @param blockedAt 차단 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record BlockedUserDTO(
		Long blockedUserId,
		String name,
		String roomNumber,
		String profileImageUrl,
		LocalDateTime blockedAt
) {
}
