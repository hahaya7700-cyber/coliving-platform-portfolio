package com.coliving.platform.chat.dto;

import java.util.List;

/**
 * 채팅방 메시지 목록 조회 API 응답.
 * 4-3 메시지 목록 (오래된 것 → 최신 순)
 *
 * @param messages 오래된 순서로 정렬한 채팅 메시지 목록
 * @param hasMore 이전 메시지가 더 있는지 여부 (예: true)
 * @author 오시문
 */
public record ChatMessageListResponseDTO(List<ChatMessageDTO> messages, boolean hasMore) {
}
