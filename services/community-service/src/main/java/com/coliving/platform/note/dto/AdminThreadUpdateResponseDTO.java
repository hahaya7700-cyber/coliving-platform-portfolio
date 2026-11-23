package com.coliving.platform.note.dto;

import com.coliving.platform.note.entity.NoteThreadStatus;
import java.time.LocalDateTime;

/**
 * 관리자 쪽지함 상태·메모 수정 API 응답.
 * 3-4 응답
 *
 * @param threadId 쪽지함 ID (예: 1)
 * @param status 현재 처리 상태 (UNANSWERED / ANSWERED 중 하나)
 * @param memo 쪽지함 내부 메모
 * @param updatedAt 수정 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record AdminThreadUpdateResponseDTO(Long threadId, NoteThreadStatus status, String memo,
		LocalDateTime updatedAt) {
}
