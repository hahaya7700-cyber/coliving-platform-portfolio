package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.ReportAction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 관리자 신고 처리 API의 조치 요청.
 * 9-3 신고 처리 (조치)
 *
 * @param action 신고 처리 조치 (REJECT / HIDE_CONTENT / DELETE_CONTENT / WARNING / RESTRICTION / SUSPENSION / OFFLINE_RECORD 중 하나), 필수
 * @param durationDays 이용 제한 기간 (1·3·7·30일, 일시 제한 조치에 사용)
 * @param hideContent 신고 대상 콘텐츠도 숨길지 여부 (예: true)
 * @param reason 제재 사유, 500자 이하
 * @param adminMemo 관리자 내부 메모
 * @author 오시문
 */
public record ReportActionRequestDTO(
		@NotNull(message = "조치를 선택해 주세요.")
		ReportAction action,

		Integer durationDays,

		Boolean hideContent,

		@Size(max = 500, message = "사유는 500자 이하로 입력해 주세요.")
		String reason,

		String adminMemo
) {
}
