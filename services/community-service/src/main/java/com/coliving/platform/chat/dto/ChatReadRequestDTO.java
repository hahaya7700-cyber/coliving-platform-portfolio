package com.coliving.platform.chat.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 채팅방 읽음 위치 갱신 API 요청.
 * 4-4 읽음 처리
 *
 * @param lastReadMessageId 마지막으로 읽은 메시지 ID (예: 507f1f77bcf86cd799439011), 필수
 * @author 오시문
 */
public record ChatReadRequestDTO(
		@NotBlank(message = "마지막으로 읽은 메시지 ID가 없습니다.")
		String lastReadMessageId
) {
}
