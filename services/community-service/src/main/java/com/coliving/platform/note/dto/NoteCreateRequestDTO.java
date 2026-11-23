package com.coliving.platform.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원 쪽지 보내기·관리자 답장 API의 본문 요청.
 * 1-2 쪽지 보내기 · 3-3 답장 보내기
 *
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~1000자
 * @author 오시문
 */
public record NoteCreateRequestDTO(
		@NotBlank(message = "내용을 입력해 주세요.")
		@Size(max = 1000, message = "1,000자 이하로 입력해 주세요.")
		String content
) {
}
