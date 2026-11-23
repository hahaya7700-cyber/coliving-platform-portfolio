/**
 * @file 대시보드 서비스 API 호출 모음 (호실 현황, 활동 로그, 요약, 모니터링)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 대시보드 서비스 관리자 API 기본 주소 */
const BASE = `${import.meta.env.VITE_BACKEND_API_BASE_URL}/dashboard-service/admin`

/**
 * 대시보드 서비스에 GET 요청을 보내고 JSON 응답을 반환 (실패하면 status가 담긴 Error를 던짐)
 * @param {string} path - 요청 경로
 * @param {Object} params - 쿼리 파라미터
 * @returns {Promise<any>} 응답 JSON
 */
async function get(path, params) {
    const query = new URLSearchParams(params).toString()
    const res = await fetch(`${BASE}${path}?${query}`, { credentials: 'include' })
    if (!res.ok) {
        const error = new Error(`요청 실패 (${res.status})`)
        error.status = res.status // 상태 코드 구분
        throw error
    }
    return res.json()
}

/**
 * 건물별 호실 현황 조회
 * @param {number} buildingId - 건물 ID
 * @returns {Promise<Array>} 호실 현황 목록
 */
export const fetchRooms = (buildingId) => get('/rooms', { buildingId })

/**
 * 건물별 최근 활동 로그 조회
 * @param {number} buildingId - 건물 ID
 * @param {number} size - 가져올 건수 (기본 10)
 * @returns {Promise<Array>} 활동 로그 목록
 */
export const fetchActivityLogs = (buildingId, size = 10) =>
    get('/activity-logs', { buildingId, size })

/**
 * "더보기" 팝업용 활동 로그 조회 (유형을 주면 그 유형만, 안 주면 전체를 한 페이지씩)
 * @param {number} buildingId - 건물 ID
 * @param {Object} options - 조회 옵션
 * @param {string|null} options.type - 활동 유형
 * @param {number} options.page - 페이지 번호 (0부터)
 * @param {number} options.size - 페이지 크기
 * @returns {Promise<Object>} 한 페이지 분량의 활동 로그 ({items, hasNext})
 */
export const fetchActivityHistory = (buildingId, {type = null, page = 0, size = 20} = {}) =>
    get('/activity-logs/history', type ? {buildingId, type, page, size} : {buildingId, page, size})

/**
 * 건물별 월 요약 조회 (지표 카드, 재무 요약, 6개월 추이)
 * @param {number} buildingId - 건물 ID
 * @param {string} month - 조회 월 (yyyy-MM, 없으면 이번 달)
 * @returns {Promise<Object>} 월 요약
 */
export const fetchSummary = (buildingId, month) =>
    get('/summary', month ? { buildingId, month } : { buildingId })

/**
 * 서비스 상태와 요청 지표 요약 조회
 * @returns {Promise<Object>} 모니터링 요약
 */
export const fetchMonitoringSummary = () => get('/monitoring/summary')

/**
 * 최근 1시간 모니터링 그래프 데이터 조회
 * @returns {Promise<Object>} 모니터링 그래프 데이터
 */
export const fetchMonitoringCharts = () => get('/monitoring/charts')