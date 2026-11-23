package com.coliving.platform.note.dto;

import com.coliving.platform.note.entity.NoteThreadStatus;
import com.coliving.platform.note.entity.NoteThreadType;
import java.time.LocalDateTime;

/**
 * 관리자 쪽지함 목록 조회 API의 응답 항목.
 * 3-1 쪽지함 목록 항목
 *
 * @param threadId 쪽지함 ID (예: 1)
 * @param type 항목 유형 (CANDIDATE / RESIDENT / MEMBER / GUEST 중 하나)
 * @param userId 회원 ID (예: 5001)
 * @param name 이름 (예: 김민서)
 * @param lastContent 마지막 쪽지 내용 미리보기
 * @param lastAt 마지막 쪽지 일시
 * @param unreadCount 안 읽은 항목 수 (예: 3)
 * @param status 현재 처리 상태 (UNANSWERED / ANSWERED 중 하나)
 * @author 오시문
 */
public record AdminThreadListItemDTO(
		Long threadId,
		NoteThreadType type,
		Long userId,
		String name,
		String lastContent,
		LocalDateTime lastAt,
		long unreadCount,
		NoteThreadStatus status
) {
}
