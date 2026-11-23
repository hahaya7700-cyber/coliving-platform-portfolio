package com.coliving.platform.faq.repository;

import com.coliving.platform.faq.entity.FaqFileType;
import com.coliving.platform.faq.entity.FaqIndexStatus;
import com.coliving.platform.faq.entity.FaqScope;
import java.time.LocalDateTime;

/**
 * 문서 목록용 — 원본 파일(file_data) 없이 필요한 칸만
 *
 * @param id 저장된 항목 ID (예: 1)
 * @param title 제목 (예: 공용 공간 이용 안내)
 * @param scope FAQ 문서 적용 범위 (COMMON / BUILDING 중 하나)
 * @param buildingId 건물 ID (예: 1)
 * @param fileName 원본 파일 이름 (예: rules.pdf)
 * @param fileType 원본 파일 형식 (PDF / DOCX / TXT / MD 중 하나)
 * @param fileSize 원본 파일 크기 (바이트, 예: 1024)
 * @param indexStatus 문서 반영 상태 (PENDING / PROCESSING / COMPLETED / FAILED 중 하나)
 * @param indexError 문서 반영 실패 사유 (없으면 null)
 * @param indexedAt 문서가 마지막으로 반영된 일시 (미반영이면 null)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @author 오시문
 */
public record FaqDocumentRow(
		Long id,
		String title,
		FaqScope scope,
		Long buildingId,
		String fileName,
		FaqFileType fileType,
		long fileSize,
		FaqIndexStatus indexStatus,
		String indexError,
		LocalDateTime indexedAt,
		LocalDateTime createdAt
) {
}
