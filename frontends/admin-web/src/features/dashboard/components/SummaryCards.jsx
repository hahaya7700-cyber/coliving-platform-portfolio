/**
 * @file 지표 카드 (입주율, 계약 승인 대기, 입주 예정, 퇴거 예정)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import Diff from './Diff.jsx'

/**
 * 지표 카드 한 장
 * @param {Object} props
 * @param {string} props.title - 카드 제목
 * @param {string} props.value - 카드에 크게 표시할 값
 * @param {React.ReactNode} props.children - 값 아래에 붙일 보조 내용
 * @returns {JSX.Element}
 */
function Card({ title, value, children }) {
    return (
        <div className="rounded-lg border border-main/20 bg-white p-5 text-center">
            <p className="text-xs text-text/70">{title}</p>
            <p className="mt-1 text-2xl font-bold text-text sm:text-3xl">{value}</p>
            {children}
        </div>
    )
}

/**
 * 값이 없으면(옛 집계 데이터) 단위 없이 "-"만 반환하고, 있으면 값에 단위를 붙임
 * @param {number|null} value - 건수
 * @param {string} unit - 단위
 * @returns {string} 표시용 문자열
 */
const count = (value, unit) => (value == null ? '-' : `${value}${unit}`)

/**
 * 지표 카드 4장
 * @param {Object} props
 * @param {Object} props.cards - 지표 카드 값 (입주율, 승인 대기, 입주 예정, 퇴거 예정)
 * @returns {JSX.Element}
 */
function SummaryCards({ cards }) {
    return (
        <div className="grid grid-cols-2 gap-3 sm:gap-4 lg:grid-cols-4">
            <Card title="입주율" value={`${cards.occupancyRate}%`}>
                <Diff value={cards.occupancyRateDiff} unit="%p" label="지난달 대비" />
            </Card>
            <Card title="계약 승인 대기" value={count(cards.pendingApprovalCount, '건')}>
                <p className="mt-1 text-xs text-text/50">처리 대기 중인 신청</p>
            </Card>
            <Card title="입주 예정" value={count(cards.moveInScheduledCount, '건')}>
                <p className="mt-1 text-xs text-text/50">입주를 앞둔 호실</p>
            </Card>
            <Card title="퇴거 예정" value={count(cards.moveOutScheduledCount, '건')}>
                <p className="mt-1 text-xs text-text/50">퇴거를 앞둔 호실</p>
            </Card>
        </div>
    )
}

export default SummaryCards