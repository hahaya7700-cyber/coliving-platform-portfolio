package com.coliving.platform.note.dto;

import java.time.LocalDateTime;

/**
 * 회원 쪽지 보내기 API 응답.
 * 1-2 응답
 *
 * @param noteId 쪽지 ID (예: 1)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record NoteCreateResponseDTO(Long noteId, LocalDateTime createdAt) {
}
