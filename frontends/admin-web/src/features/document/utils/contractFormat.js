/**
 * @file 계약서 화면용 날짜·금액 표시 유틸
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/**
 * YYYY-MM-DD 를 YYYY.MM.DD 로 변환 (값이 없으면 빈 문자열)
 * @param {string} isoDate - YYYY-MM-DD 형식의 날짜
 * @returns {string} YYYY.MM.DD 형식의 날짜
 */
export function formatDate(isoDate) {
    return isoDate ? isoDate.replaceAll('-', '.') : ''
}

/**
 * 원 단위 금액을 만 원 단위로 표시 (만 원으로 딱 떨어지지 않으면 원 단위로 표시)
 * @param {number} amount - 원 단위 금액
 * @returns {string} 표시용 문자열
 */
export function formatMoney(amount) {
    if (amount % 10000 === 0) return `${(amount / 10000).toLocaleString()}만`
    return `${amount.toLocaleString()}원`
}

/**
 * 금액을 원 단위 그대로 표시 (예: 1,741,000원)
 * @param {number} amount - 원 단위 금액
 * @returns {string} 표시용 문자열
 */
export function formatWon(amount) {
    return `${amount.toLocaleString()}원`
}