/**
 * @file 활동 로그 유형·상태 표시 정보 (카드와 더보기 팝업이 함께 사용)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 활동 유형별 표시 라벨과 색상 클래스 */
export const TYPE = {
    CONTRACT: {label: '계약', className: 'bg-main/15 text-main'},
    RESERVATION: {label: '예약', className: 'bg-accent/30 text-text'},
    MOVE_IN: {label: '입주', className: 'bg-main text-background'},
    PAYMENT: {label: '결제', className: 'bg-text/10 text-text'},
}

/* 활동 상태별 표시 라벨과 색상 클래스 */
export const STATUS = {
    COMPLETED: {label: '완료', className: 'text-main'},
    PENDING: {label: '대기', className: 'text-accent'},
    PROCESSING: {label: '처리중', className: 'text-text/60'},
}

/**
 * 활동 유형의 표시 정보 조회 (모르는 유형이면 유형 값을 라벨로 사용)
 * @param {string} activityType - 활동 유형
 * @returns {Object} 표시 정보 ({label, className})
 */
export function typeOf(activityType) {
    return TYPE[activityType] ?? {label: activityType, className: 'bg-text/10 text-text'}
}

/**
 * 활동 상태의 표시 정보 조회 (모르는 상태면 상태 값을 라벨로 사용)
 * @param {string} status - 활동 상태
 * @returns {Object} 표시 정보 ({label, className})
 */
export function statusOf(status) {
    return STATUS[status] ?? {label: status, className: 'text-text/60'}
}