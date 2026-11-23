package com.coliving.document.form.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 양식 수정 요청 (제목과 본문만 수정 가능)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record FormUpdateRequest(
        /* 제목 */
        @NotBlank @Size(max = 200) String title,
        /* 본문 */
        @NotBlank String content
) {
}