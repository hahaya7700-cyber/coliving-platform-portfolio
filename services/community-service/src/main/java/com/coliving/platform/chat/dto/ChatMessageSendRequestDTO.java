package com.coliving.platform.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST 또는 /app/chat/{roomId}로 채팅 메시지를 보내는 요청.
 * 4-5 WebSocket SEND · 4-6 REST 보내기
 *
 * @param content 내용 (예: 공용 공간 이용 문의), 필수, 1~1000자
 * @author 오시문
 */
public record ChatMessageSendRequestDTO(
		@NotBlank(message = "메시지를 입력해 주세요.")
		@Size(max = 1000, message = "메시지는 1,000자 이하로 입력해 주세요.")
		String content
) {
}
