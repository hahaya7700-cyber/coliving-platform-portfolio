package com.coliving.platform.note.dto;

import com.coliving.platform.common.entity.SenderType;
import java.time.LocalDateTime;

/**
 * 내 쪽지 목록 조회 API의 응답 항목.
 * 1-1 쪽지 항목
 *
 * @param noteId 쪽지 ID (예: 1)
 * @param senderType 발신자 유형 (USER / ADMIN 중 하나)
 * @param senderName 발신자 표시 이름 (예: 김민서)
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @param readAt 쪽지를 읽은 일시 (안 읽었으면 null)
 * @author 오시문
 */
public record NoteItemDTO(
		Long noteId,
		SenderType senderType,
		String senderName,
		String content,
		LocalDateTime createdAt,
		LocalDateTime readAt
) {
}
