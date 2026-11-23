package com.coliving.platform.faq.dto;

/**
 * FAQ 답변·질문 기록 API에 포함되는 근거 문서 응답.
 * 근거 문서 (7-1 sources, 7-8 sources)
 *
 * @param documentId FAQ 문서 ID (예: 1)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @author 오시문
 */
public record FaqSourceDTO(Long documentId, String title) {
}
