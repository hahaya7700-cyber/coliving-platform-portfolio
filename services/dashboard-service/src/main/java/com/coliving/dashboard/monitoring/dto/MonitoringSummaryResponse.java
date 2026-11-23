package com.coliving.dashboard.monitoring.dto;

import java.util.List;

/**
 * 관리자 모니터링 화면의 요약 카드와 서비스별 상태를 한 번에 내려주는 응답
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record MonitoringSummaryResponse(
        /* Prometheus 조회 성공 여부 (false면 cards는 null, services는 빈 목록) */
        boolean prometheusAvailable,
        /* 상단 요약 카드 */
        Cards cards,
        /* 서비스별 상태 */
        List<ServiceStatus> services,
        /* 조회 시각 (ISO-8601, 서울 기준) */
        String checkedAt
) {

    /* 요약 카드 4개. 값이 아직 없으면(요청 없음 등) 해당 항목은 null */
    public record Cards(
            /* 정상 서비스 수 */
            Integer upCount,
            /* 수집 대상 서비스 수 */
            Integer totalCount,
            /* 최근 5분 평균 응답 시간(ms) */
            Double avgResponseMs,
            /* 최근 5분 기준 분당 요청 수 */
            Double requestsPerMinute,
            /* 최근 5분 5xx 오류율(%) */
            Double errorRatePercent
    ) {}

    /* 서비스 하나의 상태 */
    public record ServiceStatus(
            /* 서비스 이름 (Prometheus의 service 라벨) */
            String name,
            /* 수집 대상 주소 (host:port) */
            String instance,
            /* 정상 여부 */
            boolean up
    ) {}

    /* Prometheus에 연결하지 못했을 때의 응답 */
    public static MonitoringSummaryResponse unavailable(String checkedAt) {
        return new MonitoringSummaryResponse(false, null, List.of(), checkedAt);
    }
}
