package com.coliving.platform.safety.dto;

import com.coliving.platform.safety.entity.ReportReason;
import com.coliving.platform.safety.entity.ReportSourceType;
import com.coliving.platform.safety.entity.ReportStatus;
import com.coliving.platform.safety.entity.ReportTargetType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 신고 목록 조회 API의 대상별 응답 항목.
 * 9-1 신고 목록 항목 (같은 대상 묶음)
 *
 * @param reportId 신고 ID (예: 1)
 * @param targetType 대상 종류 (POST / COMMENT / CHAT_MESSAGE / USER 중 하나)
 * @param targetId 대상 ID (채팅 메시지는 MongoDB ObjectId, 나머지는 숫자 ID 문자열)
 * @param sourceType 작성을 시작한 위치 유형 (CHAT_ROOM / POST 중 하나)
 * @param sourceId 신고 위치 ID (게시글 또는 채팅방 ID, 예: 1)
 * @param sourceTitle 신고 위치 표시 제목
 * @param targetUserId 신고 대상 회원 ID (예: 5002)
 * @param targetUserName 신고 대상 회원 표시 이름
 * @param targetRoomNumber 신고 대상 회원 호실 (예: 302호)
 * @param buildingId 건물 ID (예: 1)
 * @param buildingName 건물 이름 (예: 강남점)
 * @param contentPreview 신고 대상 내용 미리보기
 * @param reasonTypes 같은 대상에 접수된 신고 사유 목록
 * @param reportCount 같은 대상에 접수된 신고 수 (예: 2)
 * @param status 현재 처리 상태 (RECEIVED / RESOLVED / REJECTED 중 하나)
 * @param latestReportedAt 같은 대상의 최근 신고 일시
 * @author 오시문
 */
public record AdminReportItemDTO(
		Long reportId,
		ReportTargetType targetType,
		String targetId,
		ReportSourceType sourceType,
		Long sourceId,
		String sourceTitle,
		Long targetUserId,
		String targetUserName,
		String targetRoomNumber,
		Long buildingId,
		String buildingName,
		String contentPreview,
		List<ReportReason> reasonTypes,
		int reportCount,
		ReportStatus status,
		LocalDateTime latestReportedAt
) {
}
