package com.coliving.platform.faq.service;

import java.util.List;

/**
 * 반영 작업 생성 완료 → 커밋 후 비동기 반영 시작 (FaqIndexWorker)
 *
 * @param jobId 문서 반영 작업 ID (예: 1)
 * @param documentIds 반영할 문서 ID 목록 (예: [1, 2], 생략하거나 비우면 대기·실패 문서 전체)
 * @author 오시문
 */
public record FaqReindexRequestedEvent(Long jobId, List<Long> documentIds) {
}
