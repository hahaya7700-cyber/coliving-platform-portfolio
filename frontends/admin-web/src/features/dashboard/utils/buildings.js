/**
 * @file 조회 월 선택 목록과 월 표시 유틸
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/**
 * 현재 월부터 거꾸로 count개월의 월 목록 생성
 * @param {number} count - 개월 수 (기본 6)
 * @returns {string[]} yyyy-MM 형식의 월 목록
 */
export function recentMonths(count = 6) {
    const now = new Date()
    const months = []
    for (let i = 0; i < count; i++) {
        const d = new Date(now.getFullYear(), now.getMonth() - i, 1)
        months.push(`${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`)
    }
    return months
}

/**
 * yyyy-MM 을 "2026년 10월" 형태로 변환
 * @param {string} month - yyyy-MM 형식의 월
 * @returns {string} 표시용 월 문자열
 */
export function monthText(month) {
    return `${month.slice(0, 4)}년 ${Number(month.slice(5))}월`
}