package com.coliving.platform.note.dto;

import java.time.LocalDateTime;

/**
 * 비회원 문의 접수 API 응답.
 * 2-1 응답
 *
 * @param inquiryId 접수된 비회원 문의 ID (예: 1)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record GuestInquiryResponseDTO(Long inquiryId, LocalDateTime createdAt) {
}
