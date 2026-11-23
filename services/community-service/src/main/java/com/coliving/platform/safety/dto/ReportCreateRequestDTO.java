package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.ReportReason;
import com.coliving.platform.safety.entity.ReportSourceType;
import com.coliving.platform.safety.entity.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 입주자의 게시글·댓글·채팅·회원 신고 API 요청.
 * 8-1 신고하기
 *
 * @param sourceType 작성을 시작한 위치 유형 (CHAT_ROOM / POST 중 하나), 필수
 * @param sourceId 신고 위치 ID (게시글 또는 채팅방 ID, 예: 1), 필수
 * @param targetType 대상 종류 (POST / COMMENT / CHAT_MESSAGE / USER 중 하나), 필수
 * @param targetId 대상 ID (채팅 메시지는 MongoDB ObjectId, 나머지는 숫자 ID 문자열), 필수
 * @param reasonType 신고 사유 분류 (ABUSE / HARASSMENT / PRIVACY / SPAM / INAPPROPRIATE / HOUSE_RULE / ETC 중 하나), 필수
 * @param reasonDetail 신고 상세 사유 (기타 사유이면 필수), 500자 이하
 * @author 오시문
 */
public record ReportCreateRequestDTO(
		@NotNull(message = "신고 위치가 없습니다.")
		ReportSourceType sourceType,

		@NotNull(message = "신고 위치 ID가 없습니다.")
		Long sourceId,

		@NotNull(message = "신고 대상 종류가 없습니다.")
		ReportTargetType targetType,

		@NotBlank(message = "신고 대상 ID가 없습니다.")
		String targetId,

		@NotNull(message = "신고 사유를 선택해 주세요.")
		ReportReason reasonType,

		@Size(max = 500, message = "상세 내용은 500자 이하로 입력해 주세요.")
		String reasonDetail
) {
}
