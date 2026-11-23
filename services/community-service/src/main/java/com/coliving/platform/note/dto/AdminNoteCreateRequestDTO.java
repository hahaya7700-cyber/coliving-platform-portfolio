package com.coliving.platform.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 관리자가 회원에게 새 쪽지를 보내는 API 요청.
 * 3-5 새 쪽지 쓰기
 *
 * @param recipientUserId 쪽지를 받을 회원 ID (예: 5001), 필수
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~1000자
 * @author 오시문
 */
public record AdminNoteCreateRequestDTO(
		@NotNull(message = "받는 사람을 선택해 주세요.")
		Long recipientUserId,

		@NotBlank(message = "내용을 입력해 주세요.")
		@Size(max = 1000, message = "1,000자 이하로 입력해 주세요.")
		String content
) {
}
