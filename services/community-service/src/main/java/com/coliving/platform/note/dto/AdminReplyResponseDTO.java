package com.coliving.platform.note.dto;

import com.coliving.platform.note.entity.NoteThreadStatus;
import java.time.LocalDateTime;

/**
 * 관리자의 회원 문의 답장 API 응답.
 * 3-3 응답
 *
 * @param noteId 쪽지 ID (예: 1)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @param threadStatus 쪽지함 답변 상태 (UNANSWERED / ANSWERED 중 하나)
 * @author 오시문
 */
public record AdminReplyResponseDTO(Long noteId, LocalDateTime createdAt, NoteThreadStatus threadStatus) {
}
