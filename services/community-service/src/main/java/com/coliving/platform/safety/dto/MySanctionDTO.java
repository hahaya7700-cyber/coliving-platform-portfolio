package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.SanctionType;
import java.time.LocalDateTime;

/**
 * 내 제재 상태·커뮤니티 활동 조회 API의 제재 응답.
 * 8-5 내 이용 제한 상태 · 5-9 sanction
 *
 * @param active 현재 유효한 제재인지 여부 (예: true)
 * @param sanctionId 제재 ID (예: 1)
 * @param sanctionType 제재 유형 (WARNING / RESTRICTION / SUSPENSION 중 하나)
 * @param durationDays 이용 제한 기간 (1·3·7·30일, 일시 제한 조치에 사용)
 * @param reason 제재 사유
 * @param startsAt 제재 시작 일시 (예: 2026-11-12T10:00:00)
 * @param endsAt 제재 종료 일시 (예: 2026-11-15T10:00:00)
 * @author 오시문
 */
public record MySanctionDTO(
		boolean active,
		Long sanctionId,
		SanctionType sanctionType,
		Integer durationDays,
		String reason,
		LocalDateTime startsAt,
		LocalDateTime endsAt
) {

	public static MySanctionDTO none() {
		return new MySanctionDTO(false, null, null, null, null, null, null);
	}
}
