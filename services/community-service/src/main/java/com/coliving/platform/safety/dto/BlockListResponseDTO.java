package com.coliving.platform.safety.dto;

import java.util.List;

/**
 * 내 차단 목록 조회 API 응답.
 * 8-2 차단 목록 응답
 *
 * @param blocks 차단한 회원 목록
 * @author 오시문
 */
public record BlockListResponseDTO(List<BlockedUserDTO> blocks) {
}
