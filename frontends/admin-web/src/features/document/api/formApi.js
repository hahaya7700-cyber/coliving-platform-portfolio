/**
 * @file 문서 서비스 양식 API 호출 (조회·등록·수정·삭제)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 문서 서비스 양식 API 기본 주소 */
const BASE = `${import.meta.env.VITE_BACKEND_API_BASE_URL}/document-service/admin/forms`

/**
 * 문서 서비스 양식 API를 호출하고 JSON 응답 반환 (실패하면 status와 서버가 보낸 message(serverMessage)가 담긴 Error를 던지고, 204면 null 반환)
 * @param {string} path - BASE 뒤에 붙일 경로
 * @param {Object} options - fetch 옵션
 * @returns {Promise<any>} 응답 JSON 또는 null
 */
async function request(path = '', options) {
    const res = await fetch(`${BASE}${path}`, { ...options, credentials: 'include' })
    if (!res.ok) {
        const error = new Error(`요청 실패 (${res.status})`)
        error.status = res.status // 상태 코드로 화면에서 안내 문구를 고른다
        // 문서 서비스가 { message }로 이유를 보내면(틀린 빈칸 이름, 삭제할 수 없는 양식 등) 화면에 그대로 보여준다
        try {
            error.serverMessage = (await res.json()).message || ''
        } catch {
            error.serverMessage = ''
        }
        throw error
    }
    return res.status === 204 ? null : res.json()
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
 * 전체 양식 목록 조회
 * @returns {Promise<Array>} 양식 목록
 */
export const fetchForms = () => request()

/**
 * 양식 등록
 * @param {Object} data - 등록할 양식 ({language, title, content})
 * @returns {Promise<Object>} 등록된 양식
 */
export const createForm = (data) => request('', withBody('POST', data))

/**
 * 양식 수정 (제목과 본문만 수정 가능)
 * @param {number} id - 양식 ID
 * @param {Object} data - 수정할 내용 ({title, content})
 * @returns {Promise<Object>} 수정된 양식
 */
export const updateForm = (id, data) => request(`/${id}`, withBody('PUT', data))

/**
 * 양식 삭제
 * @param {number} id - 양식 ID
 * @returns {Promise<null>} 응답 본문 없음
 */
export const deleteForm = (id) => request(`/${id}`, { method: 'DELETE' })