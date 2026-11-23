package com.coliving.platform.faq.dto;

import java.util.List;

/**
 * 관리자 FAQ 문서 반영 시작 API 요청.
 * 7-6 반영 시작 — documentIds 생략 시 대기·실패 문서 전체
 *
 * @param documentIds 반영할 문서 ID 목록 (예: [1, 2], 생략하거나 비우면 대기·실패 문서 전체)
 * @author 오시문
 */
public record FaqReindexRequestDTO(List<Long> documentIds) {
}
