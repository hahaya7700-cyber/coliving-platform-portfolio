package com.coliving.platform.chat.dto;

import com.coliving.platform.chat.entity.ChatSourceType;
import jakarta.validation.constraints.NotNull;

/**
 * 입주자 간 채팅방 열기 API 요청.
 * 4-2 채팅방 열기
 *
 * @param partnerUserId 채팅 상대 회원 ID (예: 5002, 본인 제외), 필수
 * @param sourceType 작성을 시작한 위치 유형 (POST / PROFILE 중 하나), 필수
 * @param sourcePostId 채팅을 시작한 게시글 ID (sourceType이 POST이면 필수)
 * @author 오시문
 */
public record ChatRoomOpenRequestDTO(
		@NotNull(message = "채팅 상대를 선택해 주세요.")
		Long partnerUserId,

		@NotNull(message = "채팅 시작 위치가 없습니다.")
		ChatSourceType sourceType,

		Long sourcePostId
) {
}
