package com.coliving.dashboard.monitoring.dto;

import java.util.List;

/**
 * 모니터링 화면의 4개 그래프(요청 수, 응답 시간 p95, 힙 메모리, CPU)에 쓰는 시계열 응답
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record MonitoringChartsResponse(
        /* Prometheus 조회 성공 여부 (false면 모든 패널이 비어 있음) */
        boolean prometheusAvailable,
        /* 초당 요청 수 (req/s) */
        Panel requests,
        /* 응답 시간 p95 (ms) */
        Panel p95,
        /* JVM 힙 메모리 (MB) */
        Panel heap,
        /* CPU 사용률 (%) */
        Panel cpu,
        /* 조회 시각 (ISO-8601, 서울 기준) */
        String checkedAt
) {

    /* 그래프 하나. 서비스마다 선 하나(Series)가 들어간다 */
    public record Panel(List<Series> series) {}

    /* 서비스 하나의 시계열 */
    public record Series(String name, List<Point> points) {}

    /* 시계열의 점. t는 epoch 초, v는 값(없으면 null) */
    public record Point(long t, Double v) {}

    /* Prometheus에 연결하지 못했을 때의 응답 */
    public static MonitoringChartsResponse unavailable(String checkedAt) {
        Panel empty = new Panel(List.of());
        return new MonitoringChartsResponse(false, empty, empty, empty, empty, checkedAt);
    }
}
