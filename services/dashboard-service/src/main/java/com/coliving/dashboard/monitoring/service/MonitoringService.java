package com.coliving.dashboard.monitoring.service;

import com.coliving.dashboard.monitoring.client.PrometheusClient;
import com.coliving.dashboard.monitoring.client.PrometheusClient.Sample;
import com.coliving.dashboard.monitoring.dto.MonitoringChartsResponse;
import com.coliving.dashboard.monitoring.dto.MonitoringChartsResponse.Panel;
import com.coliving.dashboard.monitoring.dto.MonitoringChartsResponse.Point;
import com.coliving.dashboard.monitoring.dto.MonitoringChartsResponse.Series;
import com.coliving.dashboard.monitoring.dto.MonitoringSummaryResponse;
import com.coliving.dashboard.monitoring.dto.MonitoringSummaryResponse.Cards;
import com.coliving.dashboard.monitoring.dto.MonitoringSummaryResponse.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Prometheus에서 서비스 상태와 요청 지표를 조회해 모니터링 화면용 데이터를 만든다
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringService {

    /* 수집 대상 job 이름 (compose의 Prometheus 설정 job_name과 같아야 함) */
    private static final String JOB = "spring-services";

    /* 요청 수 지표 (Prometheus가 긁어가는 /actuator 요청은 통계에서 제외) */
    private static final String REQUESTS =
            "http_server_requests_seconds_count{job=\"" + JOB + "\",uri!~\"/actuator.*\"}";

    /* 5xx 오류 요청 수 지표 */
    private static final String REQUESTS_5XX =
            "http_server_requests_seconds_count{job=\"" + JOB + "\",uri!~\"/actuator.*\",status=~\"5..\"}";

    /* 응답 시간 합계 지표 */
    private static final String DURATION_SUM =
            "http_server_requests_seconds_sum{job=\"" + JOB + "\",uri!~\"/actuator.*\"}";

    /* 응답 시간 구간(bucket) 지표 */
    private static final String DURATION_BUCKET =
            "http_server_requests_seconds_bucket{job=\"" + JOB + "\",uri!~\"/actuator.*\"}";

    /* 서비스 상태(up) 조회 쿼리 */
    private static final String Q_UP = "up{job=\"" + JOB + "\"}";

    /* 최근 5분 평균 응답 시간(초) 쿼리 */
    private static final String Q_AVG_SECONDS = "sum(rate(" + DURATION_SUM + "[5m])) / sum(rate(" + REQUESTS + "[5m]))";

    /* 최근 5분 기준 분당 요청 수 쿼리 */
    private static final String Q_REQUESTS_PER_MINUTE = "sum(rate(" + REQUESTS + "[5m])) * 60";

    /* 최근 5분 5xx 오류율 쿼리 */
    private static final String Q_ERROR_RATIO =
            "(sum(rate(" + REQUESTS_5XX + "[5m])) or vector(0)) / sum(rate(" + REQUESTS + "[5m]))";

    /* 서비스별 초당 요청 수 그래프 쿼리 */
    private static final String Q_CHART_REQUESTS = "sum by (service) (rate(" + REQUESTS + "[1m]))";

    /* 서비스별 응답 시간 p95 그래프 쿼리 */
    private static final String Q_CHART_P95 =
            "histogram_quantile(0.95, sum by (le, service) (rate(" + DURATION_BUCKET + "[5m])))";

    /* 서비스별 JVM 힙 사용량 그래프 쿼리 */
    private static final String Q_CHART_HEAP =
            "sum by (service) (jvm_memory_used_bytes{job=\"" + JOB + "\",area=\"heap\"})";

    /* 서비스별 CPU 사용률 그래프 쿼리 */
    private static final String Q_CHART_CPU = "process_cpu_usage{job=\"" + JOB + "\"}";

    /* 그래프 조회 구간 (최근 60분) */
    private static final Duration CHART_RANGE = Duration.ofMinutes(60);

    /* 그래프 점 사이 간격(초) */
    private static final int CHART_STEP_SECONDS = 30;

    /* Prometheus 클라이언트 */
    private final PrometheusClient prometheus;

    /* 서비스 상태와 최근 5분 요청 지표 요약 조회 (Prometheus 실패 시 unavailable 응답) */
    public MonitoringSummaryResponse getSummary() {
        String checkedAt = now();
        try {
            List<ServiceStatus> services = prometheus.query(Q_UP).stream()
                    .map(MonitoringService::toServiceStatus)
                    .sorted(Comparator.comparing(ServiceStatus::name))
                    .toList();

            int upCount = (int) services.stream().filter(ServiceStatus::up).count();

            Double avgSeconds = single(prometheus.query(Q_AVG_SECONDS));
            Double errorRatio = single(prometheus.query(Q_ERROR_RATIO));

            Cards cards = new Cards(
                    upCount,
                    services.size(),
                    round1(avgSeconds == null ? null : avgSeconds * 1000),
                    round1(single(prometheus.query(Q_REQUESTS_PER_MINUTE))),
                    round1(errorRatio == null ? null : errorRatio * 100)
            );
            return new MonitoringSummaryResponse(true, cards, services, checkedAt);

        } catch (RuntimeException e) {
            // Prometheus가 꺼져 있거나 응답이 이상해도 화면은 안내 문구를 보여줄 수 있게 200으로 응답한다.
            log.warn("Prometheus 조회 실패: {}", e.getMessage());
            return MonitoringSummaryResponse.unavailable(checkedAt);
        }
    }

    /* 최근 1시간의 그래프 데이터. 요청 수, p95, 힙, CPU를 서비스별 시계열로 내려준다 */
    public MonitoringChartsResponse getCharts() {
        String checkedAt = now();
        Instant end = Instant.now();
        Instant start = end.minus(CHART_RANGE);
        try {
            return new MonitoringChartsResponse(
                    true,
                    panel(Q_CHART_REQUESTS, 1.0, start, end),
                    panel(Q_CHART_P95, 1000.0, start, end),               // 초 → 밀리초
                    panel(Q_CHART_HEAP, 1.0 / (1024 * 1024), start, end), // 바이트 → MB
                    panel(Q_CHART_CPU, 100.0, start, end),                // 0~1 → %
                    checkedAt
            );
        } catch (RuntimeException e) {
            log.warn("Prometheus 구간 조회 실패: {}", e.getMessage());
            return MonitoringChartsResponse.unavailable(checkedAt);
        }
    }

    /* PromQL 구간 조회 결과를 서비스별 시계열 그래프 하나로 변환 (scale 배율 적용) */
    private Panel panel(String promql, double scale, Instant start, Instant end) {
        List<Series> series = prometheus.queryRange(promql, start, end, CHART_STEP_SECONDS).stream()
                .map(s -> new Series(
                        seriesName(s.labels()),
                        s.points().stream()
                                .map(p -> new Point(p.epochSecond(), scaled(p.value(), scale)))
                                .toList()))
                .sorted(Comparator.comparing(Series::name))
                .toList();
        return new Panel(series);
    }

    /* 시계열 라벨에서 서비스 이름 추출 (없으면 instance, 그것도 없으면 unknown) */
    private static String seriesName(Map<String, String> labels) {
        return labels.getOrDefault("service", labels.getOrDefault("instance", "unknown"));
    }

    /* NaN/무한대는 JSON으로 보낼 수 없으므로 null로 바꾸고, 나머지는 소수 둘째 자리까지 반올림한다 */
    private static Double scaled(double value, double scale) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return null;
        return Math.round(value * scale * 100) / 100.0;
    }

    /* 조회 결과 한 줄을 서비스 상태로 변환 */
    private static ServiceStatus toServiceStatus(Sample sample) {
        String instance = sample.labels().getOrDefault("instance", "unknown");
        String name = sample.labels().getOrDefault("service", instance);
        return new ServiceStatus(name, instance, sample.value() == 1.0);
    }

    /* 값이 하나인 조회 결과에서 숫자를 꺼낸다. 결과가 없거나 NaN/무한대면 null */
    private static Double single(List<Sample> samples) {
        if (samples.isEmpty()) return null;
        double value = samples.get(0).value();
        return Double.isNaN(value) || Double.isInfinite(value) ? null : value;
    }

    /* 소수 첫째 자리 반올림 (null은 그대로) */
    private static Double round1(Double value) {
        return value == null ? null : Math.round(value * 10) / 10.0;
    }

    /* 서울 기준 현재 시각 문자열 반환 */
    private static String now() {
        return OffsetDateTime.now(ZoneId.of("Asia/Seoul")).toString();
    }
}
