package com.coliving.dashboard.snapshot.dto;

import java.util.List;

/**
 * 대시보드 화면의 지표 카드, 재무 요약, 6개월 추이를 한 번에 내려주는 응답
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public record DashboardSummaryResponse(
        /* 건물 ID */
        Long buildingId,
        /* 건물명 */
        String buildingName,
        /* 조회 월 (yyyy-MM) */
        String month,
        /* 위쪽 지표 카드 */
        Cards cards,
        /* 아래쪽 재무 카드 */
        Finance finance,
        /* 최근 6개월 추이 */
        List<TrendPoint> trend
) {

    /* 위쪽 지표 카드 3개 (입주율, 계약 승인 대기, 입주·퇴거 예정) */
    public record Cards(
            /* 입주율 */
            Double occupancyRate,
            /* 입주율 지난달 대비 증감 */
            Double occupancyRateDiff,
            /* 계약 승인 대기 신청 수 */
            Integer pendingApprovalCount,
            /* 입주 예정 호실 수 (옛 스냅샷은 null) */
            Integer moveInScheduledCount,
            /* 퇴거 예정 호실 수 */
            Integer moveOutScheduledCount
    ) {}

    /* 아래쪽 재무 카드 4개 (수납액, 환불, 실 수납액, 보증금 보관액). 수납·환불·실 수납은 이용료 기준이다 */
    public record Finance(
            /* 이번 달 수납액 (보증금 제외) */
            Long monthCollectedAmount,
            /* 수납액 지난달 대비 증감률 */
            Double collectedChangeRate,
            /* 이번 달 환불 완료 금액 (보증금 제외) */
            Long monthRefundedAmount,
            /* 이번 달 환불 완료 건수 */
            Integer monthRefundedCount,
            /* 실 수납액 (수납액 - 환불) */
            Long netCollectedAmount,
            /* 보증금 보관액 */
            Long depositHeldAmount
    ) {}

    /* 6개월 추이 그래프의 점 하나 */
    public record TrendPoint(
            /* 월 (yyyy-MM) */
            String month,
            /* 입주율 */
            Double occupancyRate,
            /* 수납액 */
            Long collectedAmount,
            /* 환불액 */
            Long refundedAmount
    ) {}
}