package com.coliving.platform.note.dto;

import com.coliving.platform.note.entity.NoteThreadStatus;
import jakarta.validation.constraints.Size;

/**
 * 관리자 쪽지함 상태·메모 수정 API 요청.
 * 3-4 처리 상태 · 메모 변경 (보낸 필드만 변경)
 *
 * @param status 현재 처리 상태 (UNANSWERED / ANSWERED 중 하나)
 * @param memo 쪽지함 내부 메모, 1000자 이하
 * @author 오시문
 */
public record AdminThreadUpdateRequestDTO(
		NoteThreadStatus status,
		@Size(max = 1000, message = "메모는 1,000자 이하로 입력해 주세요.")
		String memo
) {
}
