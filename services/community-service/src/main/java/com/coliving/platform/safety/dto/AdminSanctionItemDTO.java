package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.SanctionType;
import java.time.LocalDateTime;

/**
 * 관리자 제재 목록 조회 API의 응답 항목.
 * 9-4 제재 목록 항목
 *
 * @param sanctionId 제재 ID (예: 1)
 * @param userId 회원 ID (예: 5001)
 * @param userName 회원 이름 (예: 김민서)
 * @param roomNumber 호실 (예: 302호)
 * @param buildingName 건물 이름 (예: 강남점)
 * @param sanctionType 제재 유형 (WARNING / RESTRICTION / SUSPENSION 중 하나)
 * @param durationDays 이용 제한 기간 (1·3·7·30일, 일시 제한 조치에 사용)
 * @param reason 제재 사유
 * @param reportId 신고 ID (예: 1)
 * @param startsAt 제재 시작 일시 (예: 2026-11-12T10:00:00)
 * @param endsAt 제재 종료 일시 (예: 2026-11-15T10:00:00)
 * @param active 현재 유효한 제재인지 여부 (예: true)
 * @param createdByName 제재를 등록한 관리자 이름
 * @param revokedAt 제재 해제 일시 (해제하지 않았으면 null)
 * @param revokeReason 제재 해제 사유 (예: 이의 신청 수용)
 * @author 오시문
 */
public record AdminSanctionItemDTO(
		Long sanctionId,
		Long userId,
		String userName,
		String roomNumber,
		String buildingName,
		SanctionType sanctionType,
		Integer durationDays,
		String reason,
		Long reportId,
		LocalDateTime startsAt,
		LocalDateTime endsAt,
		boolean active,
		String createdByName,
		LocalDateTime revokedAt,
		String revokeReason
) {
}
