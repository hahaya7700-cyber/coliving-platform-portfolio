package com.coliving.platform.safety.dto;

import com.coliving.platform.common.entity.ContentStatus;
import com.coliving.platform.safety.entity.ReportAction;
import com.coliving.platform.safety.entity.ReportReason;
import com.coliving.platform.safety.entity.ReportSourceType;
import com.coliving.platform.safety.entity.ReportStatus;
import com.coliving.platform.safety.entity.ReportTargetType;
import com.coliving.platform.safety.entity.SanctionType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 신고 상세 조회 API 응답.
 * 9-2 신고 상세
 *
 * @param target 신고 대상 정보
 * @param targetUser 신고 대상 회원 정보
 * @param reports 신고자 식별 정보를 제외한 신고 내역
 * @param history 신고 대상 회원의 신고·제재 이력
 * @param status 현재 처리 상태 (RECEIVED / RESOLVED / REJECTED 중 하나)
 * @param action 신고 처리 조치 (REJECT / HIDE_CONTENT / DELETE_CONTENT / WARNING / RESTRICTION / SUSPENSION / OFFLINE_RECORD 중 하나)
 * @param adminMemo 관리자 내부 메모
 * @param handledByName 신고를 처리한 관리자 이름
 * @param handledAt 신고 처리 일시 (미처리면 null)
 * @author 오시문
 */
public record AdminReportDetailDTO(
		TargetInfo target,
		TargetUserInfo targetUser,
		List<ReportInfo> reports,
		HistoryInfo history,
		ReportStatus status,
		ReportAction action,
		String adminMemo,
		String handledByName,
		LocalDateTime handledAt
) {

	/**
	 * 신고 상세 API의 대상 콘텐츠와 현재 상태 응답.
	 *
	 * @param targetType 대상 종류 (POST / COMMENT / CHAT_MESSAGE / USER 중 하나)
	 * @param targetId 대상 ID (채팅 메시지는 MongoDB ObjectId, 나머지는 숫자 ID 문자열)
	 * @param sourceType 작성을 시작한 위치 유형 (CHAT_ROOM / POST 중 하나)
	 * @param sourceId 신고 위치 ID (게시글 또는 채팅방 ID, 예: 1)
	 * @param sourceTitle 신고 위치 표시 제목
	 * @param snapshot 신고 당시 저장한 대상 내용
	 * @param currentStatus 신고 대상의 현재 공개 상태 (ACTIVE / HIDDEN / DELETED 중 하나)
	 * @author 오시문
	 */
	public record TargetInfo(ReportTargetType targetType, String targetId, ReportSourceType sourceType, Long sourceId,
			String sourceTitle, ReportSnapshot snapshot, ContentStatus currentStatus) {
	}

	/**
	 * 신고 상세 API의 대상 회원 표시 정보.
	 *
	 * @param userId 회원 ID (예: 5001)
	 * @param name 이름 (예: 김민서)
	 * @param roomNumber 호실 (예: 302호)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @author 오시문
	 */
	public record TargetUserInfo(Long userId, String name, String roomNumber, String buildingName) {
	}

	/**
	 * 신고 상세 API의 익명 신고 내역 응답.
	 * 신고자 정보는 넣지 않음 (익명)
	 *
	 * @param reportId 신고 ID (예: 1)
	 * @param reasonType 신고 사유 분류 (ABUSE / HARASSMENT / PRIVACY / SPAM / INAPPROPRIATE / HOUSE_RULE / ETC 중 하나)
	 * @param reasonDetail 신고 상세 사유 (기타 사유이면 필수)
	 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
	 * @author 오시문
	 */
	public record ReportInfo(Long reportId, ReportReason reasonType, String reasonDetail, LocalDateTime createdAt) {
	}

	/**
	 * 신고 상세 API의 대상 회원 누적 신고·제재 응답.
	 *
	 * @param reportedCount 누적 신고 수 (예: 3)
	 * @param warningCount 누적 경고 수 (예: 1)
	 * @param sanctions 제재 이력 목록
	 * @author 오시문
	 */
	public record HistoryInfo(long reportedCount, long warningCount, List<SanctionHistoryInfo> sanctions) {
	}

	/**
	 * 신고 상세 API의 개별 제재 이력 응답.
	 *
	 * @param sanctionId 제재 ID (예: 1)
	 * @param sanctionType 제재 유형 (WARNING / RESTRICTION / SUSPENSION 중 하나)
	 * @param durationDays 이용 제한 기간 (1·3·7·30일, 일시 제한 조치에 사용)
	 * @param reason 제재 사유
	 * @param startsAt 제재 시작 일시 (예: 2026-11-12T10:00:00)
	 * @param endsAt 제재 종료 일시 (예: 2026-11-15T10:00:00)
	 * @param revokedAt 제재 해제 일시 (해제하지 않았으면 null)
	 * @author 오시문
	 */
	public record SanctionHistoryInfo(Long sanctionId, SanctionType sanctionType, Integer durationDays, String reason,
			LocalDateTime startsAt, LocalDateTime endsAt, LocalDateTime revokedAt) {
	}
}
