package com.coliving.platform.faq.dto;

import com.coliving.platform.faq.entity.FaqIndexJobEntity;
import com.coliving.platform.faq.entity.FaqIndexStatus;
import java.time.LocalDateTime;

/**
 * FAQ 문서 반영 시작·진행 상태 조회 API의 작업 응답.
 * 7-6 응답 · 7-7 진행 상태
 *
 * @param jobId 문서 반영 작업 ID (예: 1)
 * @param status 현재 처리 상태 (PENDING / PROCESSING / COMPLETED / FAILED 중 하나)
 * @param processedCount 처리한 문서 수 (예: 2)
 * @param totalCount 전체 항목 수 (예: 10)
 * @param errorMessage 반영 작업 실패 사유 (실패하지 않았으면 null)
 * @param startedAt 작업 시작 일시 (예: 2026-11-12T10:00:00)
 * @param finishedAt 작업 종료 일시 (진행 중이면 null)
 * @author 오시문
 */
public record FaqIndexJobDTO(
		Long jobId,
		FaqIndexStatus status,
		int processedCount,
		int totalCount,
		String errorMessage,
		LocalDateTime startedAt,
		LocalDateTime finishedAt
) {

	public static FaqIndexJobDTO from(FaqIndexJobEntity job) {
		return new FaqIndexJobDTO(job.getId(), job.getStatus(), job.getProcessedCount(), job.getTotalCount(),
				job.getErrorMessage(), job.getCreatedAt(), job.getFinishedAt());
	}
}
