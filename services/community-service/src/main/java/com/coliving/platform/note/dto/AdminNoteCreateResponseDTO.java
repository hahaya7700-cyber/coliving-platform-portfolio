package com.coliving.platform.note.dto;

import java.time.LocalDateTime;

/**
 * 관리자의 새 쪽지 작성 API 응답.
 * 3-5 응답
 *
 * @param threadId 쪽지함 ID (예: 1)
 * @param noteId 쪽지 ID (예: 1)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record AdminNoteCreateResponseDTO(Long threadId, Long noteId, LocalDateTime createdAt) {
}
