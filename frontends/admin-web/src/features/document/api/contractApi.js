/**
 * @file 관리자 계약서 목록·상세·승인·반려 API 호출
 * 신청서·계약서는 계약 서비스가 저장하고 있으므로 계약 서비스 관리자 API를 바로 부른다.
 *   admin-web → Gateway(/contract-service/admin/** , ROLE_ADMIN) → contract-service /admin/applications
 *   처리한 관리자 ID는 Gateway가 로그인 토큰에서 꺼내 X-User 헤더로 넣어준다.
 * 상태 묶음(검토 대기·서명 대기·계약 완료·반려)과 배지 글자는 관리자 화면이 정한다 (계약 서비스는 상태 값만 내려줌).
 * 화면(pages)은 statusGroup · statusLabel · reviewable을 그대로 쓰므로 여기서 붙여서 돌려준다.
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 계약 서비스 관리자 신청서 API 기본 주소 */
const BASE = `${import.meta.env.VITE_BACKEND_API_BASE_URL}/contract-service/admin/applications`

/* 화면 필터·배지 묶음 → 계약 상태(ContractStatus) 값 (여기 없는 상태(DRAFT, CANCELED 등)는 목록에 나오지 않음) */
export const STATUS_GROUPS = {
    REVIEW_PENDING: { label: '검토 대기', statuses: ['SUBMITTED'] },
    SIGN_PENDING: { label: '서명 대기', statuses: ['APPROVED'] },
    COMPLETED: { label: '계약 완료', statuses: ['PAID', 'COMPLETED'] },
    REJECTED: { label: '반려', statuses: ['REJECTED'] },
}

/* 목록에 나오는 모든 계약 상태 값 */
const ALL_STATUSES = Object.values(STATUS_GROUPS).flatMap((g) => g.statuses)

/**
 * 계약 상태가 속한 상태 묶음의 키 반환 (없으면 null)
 * @param {string} status - 계약 상태 값
 * @returns {string|null} 상태 묶음 키
 */
const groupOf = (status) =>
    Object.keys(STATUS_GROUPS).find((key) => STATUS_GROUPS[key].statuses.includes(status)) ?? null

/**
 * 계약 서비스 응답에 화면용 필드(statusGroup, statusLabel)를 붙임
 * @param {Object} item - 계약 서비스 응답 한 건
 * @returns {Object} 화면용 필드가 붙은 계약
 */
function withStatusView(item) {
    const group = groupOf(item.status)
    return { ...item, statusGroup: group, statusLabel: group ? STATUS_GROUPS[group].label : item.status }
}

/**
 * 계약 서비스 관리자 API를 호출하고 JSON 응답 반환 (실패하면 status가 담긴 Error를 던짐)
 * @param {string} path - BASE 뒤에 붙일 경로
 * @param {Object} options - fetch 옵션
 * @returns {Promise<any>} 응답 JSON
 */
async function request(path = '', options) {
    const res = await fetch(`${BASE}${path}`, { ...options, credentials: 'include' })
    if (!res.ok) {
        const error = new Error(`요청 실패 (${res.status})`)
        error.status = res.status // 상태 코드로 화면에서 안내 문구를 고른다
        throw error
    }
    return res.json()
}

/**
 * JSON 본문을 가진 요청 옵션 생성
 * @param {string} method - HTTP 메서드
 * @param {Object} data - 본문 데이터
 * @returns {Object} fetch 옵션
 */
const withBody = (method, data) => ({
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
})

/**
 * 계약 목록 조회 (keyword는 이름·건물명·호실·신청번호 검색, status는 STATUS_GROUPS의 키이고 비우면 네 묶음 전체)
 * @param {Object} params - 검색 조건
 * @param {string} params.keyword - 검색어
 * @param {string} params.status - 상태 묶음 키
 * @returns {Promise<Array>} 화면용 필드가 붙은 계약 목록
 */
export async function fetchContracts({ keyword = '', status = '' } = {}) {
    const params = new URLSearchParams()
    if (keyword) params.set('keyword', keyword)
    params.set('statuses', (STATUS_GROUPS[status]?.statuses ?? ALL_STATUSES).join(','))
    const list = await request(`?${params}`)
    return list.map(withStatusView)
}

/**
 * 계약 상세 조회 (reviewable은 검토 대기(SUBMITTED)일 때만 true, memberId는 계약 서비스의 userId를 화면용 이름으로 바꾼 값)
 * @param {number} id - 신청 ID
 * @returns {Promise<Object>} 계약 상세
 */
export async function fetchContract(id) {
    const d = await request(`/${id}`)
    return { ...withStatusView(d), reviewable: d.status === 'SUBMITTED', memberId: d.userId }
}

/**
 * 계약 승인. 승인은 결과 요약만 오므로 화면 갱신용으로 바뀐 상세를 다시 불러와 반환
 * @param {number} id - 신청 ID
 * @returns {Promise<Object>} 바뀐 계약 상세
 */
export async function approveContract(id) {
    await request(`/${id}/approve`, { method: 'POST' })
    return fetchContract(id)
}

/**
 * 계약 반려. 결과 요약만 오므로 화면 갱신용으로 바뀐 상세를 다시 불러와 반환
 * @param {number} id - 신청 ID
 * @param {string} reason - 반려 사유
 * @returns {Promise<Object>} 바뀐 계약 상세
 */
export async function rejectContract(id, reason) {
    await request(`/${id}/reject`, withBody('POST', { reason }))
    return fetchContract(id)
}
