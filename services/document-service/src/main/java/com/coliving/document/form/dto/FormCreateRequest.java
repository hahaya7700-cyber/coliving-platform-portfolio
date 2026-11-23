package com.coliving.document.form.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 양식 등록 요청 (언어는 만들 때만 지정 가능)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record FormCreateRequest(
        /* 양식 언어 (ko, en만 허용) */
        @Pattern(regexp = "ko|en") String language,
        /* 제목 */
        @NotBlank @Size(max = 200) String title,
        /* 본문 */
        @NotBlank String content
) {
}