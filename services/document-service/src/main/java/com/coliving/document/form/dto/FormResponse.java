package com.coliving.document.form.dto;

import com.coliving.document.form.entity.DocumentForm;

import java.time.OffsetDateTime;

/**
 * 양식 조회·등록·수정 응답 (엔티티를 그대로 내보내지 않고 응답 틀로 변환)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record FormResponse(
        /* 양식 ID */
        Long id,
        /* 양식 언어 */
        String language,
        /* 제목 */
        String title,
        /* 본문 */
        String content,
        /* 생성 시각 */
        OffsetDateTime createdAt,
        /* 최종 수정 시각 */
        OffsetDateTime updatedAt
) {
    /* 엔티티를 응답 틀로 옮긴다 */
    public static FormResponse from(DocumentForm form) {
        return new FormResponse(form.getId(), form.getLanguage(), form.getTitle(),
                form.getContent(), form.getCreatedAt(), form.getUpdatedAt());
    }
}