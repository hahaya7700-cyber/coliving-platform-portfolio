package com.coliving.platform.faq.dto;

import com.coliving.platform.faq.entity.FaqIndexStatus;
import java.time.LocalDateTime;

/**
 * 관리자 FAQ 문서 업로드 API 응답.
 * 7-3 업로드 응답
 *
 * @param documentId FAQ 문서 ID (예: 1)
 * @param indexStatus 문서 반영 상태 (PENDING / PROCESSING / COMPLETED / FAILED 중 하나)
 * @param uploadedAt 문서 업로드 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record FaqDocumentUploadResponseDTO(Long documentId, FaqIndexStatus indexStatus, LocalDateTime uploadedAt) {
}
