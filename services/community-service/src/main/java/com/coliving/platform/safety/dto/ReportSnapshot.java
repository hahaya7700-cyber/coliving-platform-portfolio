package com.coliving.platform.safety.dto;

/**
 * 신고 상세 조회 API에서 제공하는 신고 당시 내용 응답.
 * 신고 당시 내용 (reports.content_snapshot JSON)
 *
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param authorName 작성자 표시 이름 (예: 김민서)
 * @param writtenAt 신고 대상 작성 일시 문자열 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record ReportSnapshot(
		String content,
		String authorName,
		String writtenAt
) {
}
