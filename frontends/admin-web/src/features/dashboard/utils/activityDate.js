/**
 * @file 활동 로그의 시간·날짜 표시 유틸
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/**
 * ISO 시각을 24시간제 HH:mm 으로 표시
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {string} HH:mm 형식의 시각
 */
export function formatTime(iso) {
    return new Date(iso).toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
    })
}

/**
 * ISO 시각을 월/일로 표시 (예: 10/27)
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {string} M/D 형식의 날짜
 */
export function formatMonthDay(iso) {
    const date = new Date(iso)
    return `${date.getMonth() + 1}/${date.getDate()}`
}

/**
 * 시각을 그날 0시로 맞춘 Date 반환
 * @param {Date} date - 기준 Date
 * @returns {Date} 그날 0시의 Date
 */
function startOfDay(date) {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

/**
 * 오늘 기준으로 며칠 전인지 계산 (오늘은 0, 시계 오차로 미래가 되면 0으로 봄)
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {number} 며칠 전인지
 */
export function daysAgo(iso) {
    const diff = Math.round((startOfDay(new Date()) - startOfDay(new Date(iso))) / 86400000)
    return Math.max(0, diff)
}

/**
 * 카드용 날짜 라벨. 오늘이 아닌 건에만 "1일 전", "2일 전"을 붙이고 오늘이면 빈 문자열
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {string} 날짜 라벨
 */
export function dayAgoLabel(iso) {
    const days = daysAgo(iso)
    return days === 0 ? '' : `${days}일 전`
}

/**
 * 팝업 날짜 구분선용 라벨 (예: "오늘", "1일 전 · 10/27")
 * @param {string} iso - ISO-8601 시각 문자열
 * @returns {string} 구분선 라벨
 */
export function daySeparatorLabel(iso) {
    const days = daysAgo(iso)
    return days === 0 ? '오늘' : `${days}일 전 · ${formatMonthDay(iso)}`
}