/**
 * @file 계약 상태 배지
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 상태 묶음별 배지 색 (group은 REVIEW_PENDING | SIGN_PENDING | COMPLETED | REJECTED) */
const STYLE = {
    REVIEW_PENDING: 'bg-amber-100 text-amber-800',
    SIGN_PENDING: 'bg-sky-100 text-sky-800',
    COMPLETED: 'bg-main text-background',
    REJECTED: 'bg-red-100 text-red-700',
}

/**
 * 계약 상태 배지
 * @param {Object} props
 * @param {string} props.group - 상태 묶음 키
 * @param {string} props.label - 배지에 표시할 글자
 * @returns {JSX.Element}
 */
function ContractStatusBadge({ group, label }) {
    return (
        <span className={`shrink-0 rounded px-2 py-0.5 text-xs font-medium ${STYLE[group] ?? 'bg-main/10 text-main'}`}>
            {label}
        </span>
    )
}

export default ContractStatusBadge