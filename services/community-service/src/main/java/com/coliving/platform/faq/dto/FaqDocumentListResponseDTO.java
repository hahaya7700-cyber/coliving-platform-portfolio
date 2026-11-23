package com.coliving.platform.faq.dto;

import com.coliving.platform.faq.entity.FaqFileType;
import com.coliving.platform.faq.entity.FaqIndexStatus;
import com.coliving.platform.faq.entity.FaqScope;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 FAQ 문서 목록·반영 상태 조회 API 응답.
 * 7-2 문서 목록 · 반영 상태
 *
 * @param summary 목록 전체 집계 정보
 * @param documents FAQ 문서 목록
 * @author 오시문
 */
public record FaqDocumentListResponseDTO(Summary summary, List<Item> documents) {

	/**
	 * 목록 조회 API의 전체 집계 응답.
	 * lastIndexedAt 마지막 반영 일시 / indexedCount 반영 완료 문서 수 / pendingCount 대기·실패 문서 수
	 *
	 * @param lastIndexedAt 가장 최근 문서 반영 일시 (반영 이력이 없으면 null)
	 * @param indexedCount 반영 완료 문서 수 (예: 3)
	 * @param pendingCount 대기·실패 문서 수 (예: 2)
	 * @author 오시문
	 */
	public record Summary(LocalDateTime lastIndexedAt, long indexedCount, long pendingCount) {
	}

	/**
	 * 목록 조회 API의 개별 응답 항목.
	 *
	 * @param documentId FAQ 문서 ID (예: 1)
	 * @param title 제목 (예: 공용 공간 이용 안내)
	 * @param scope FAQ 문서 적용 범위 (COMMON / BUILDING 중 하나)
	 * @param buildingId 건물 ID (예: 1)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @param fileName 원본 파일 이름 (예: rules.pdf)
	 * @param fileType 원본 파일 형식 (PDF / DOCX / TXT / MD 중 하나)
	 * @param fileSize 원본 파일 크기 (바이트, 예: 1024)
	 * @param uploadedAt 문서 업로드 일시 (예: 2026-11-12T10:00:00)
	 * @param indexStatus 문서 반영 상태 (PENDING / PROCESSING / COMPLETED / FAILED 중 하나)
	 * @param indexedAt 문서가 마지막으로 반영된 일시 (미반영이면 null)
	 * @param indexError 문서 반영 실패 사유 (없으면 null)
	 * @author 오시문
	 */
	public record Item(
			Long documentId,
			String title,
			FaqScope scope,
			Long buildingId,
			String buildingName,
			String fileName,
			FaqFileType fileType,
			long fileSize,
			LocalDateTime uploadedAt,
			FaqIndexStatus indexStatus,
			LocalDateTime indexedAt,
			String indexError
	) {
	}
}
