package com.coliving.platform.chat.dto;

/**
 * 채팅방 열기 API의 방 ID와 생성 여부 응답.
 * 4-2 응답 (created = 새로 만든 방이면 true → 201)
 *
 * @param roomId 채팅방 ID (예: 1)
 * @param created 채팅방을 새로 만들었는지 여부 (true: 201, false: 200)
 * @author 오시문
 */
public record ChatRoomOpenResponseDTO(Long roomId, boolean created) {
}
