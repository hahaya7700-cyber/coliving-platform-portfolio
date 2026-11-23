/**
 * @file 금액 표시 유틸 (만원, 억원 단위)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/**
 * 원 단위 금액을 만원 단위로 표시 (값이 없으면 "-")
 * @param {number|null} amount - 원 단위 금액
 * @returns {string} 표시용 문자열
 */
export function formatMan(amount) {
    if (amount == null) return '-'
    return `${Math.round(amount / 10000).toLocaleString('ko-KR')}만원`
}

/**
 * 원 단위 금액을 억원 단위로 표시 (1억 미만은 만원 단위, 값이 없으면 "-")
 * @param {number|null} amount - 원 단위 금액
 * @returns {string} 표시용 문자열
 */
export function formatEok(amount) {
    if (amount == null) return '-'
    if (amount >= 100000000) return `${(amount / 100000000).toFixed(1)}억원`
    return `${Math.round(amount / 10000).toLocaleString('ko-KR')}만원`
}