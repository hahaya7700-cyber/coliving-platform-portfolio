/**
 * @file 월별 수납·환불 막대 그래프 (최근 6개월)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { formatMan } from '../utils/format'

/* 주 색상 (수납 막대) */
const MAIN = '#52796F'

/* 보조 색상 (환불 막대) */
const ACCENT = '#D6A56D'

/**
 * yyyy-MM 에서 월 숫자만 뽑아 "10월" 형태로 변환
 * @param {string} m - yyyy-MM 형식의 월
 * @returns {string} 월 라벨
 */
const monthLabel = (m) => `${Number(m.slice(5))}월`

/**
 * 월별 수납·환불 막대 그래프
 * @param {Object} props
 * @param {Array} props.trend - 최근 6개월 추이 (month, collectedAmount, refundedAmount)
 * @returns {JSX.Element}
 */
function CollectionChart({ trend }) {
    const data = trend.map((t) => ({
        name: monthLabel(t.month),
        collected: t.collectedAmount,
        refunded: t.refundedAmount,
    }))

    return (
        <section className="rounded-lg border border-main/20 bg-white p-5">
            <div className="mb-3 flex items-baseline justify-between">
                <h2 className="text-base font-semibold text-text">월별 수납·환불</h2>
                <span className="text-xs text-text/50">최근 6개월</span>
            </div>
            <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                        <YAxis
                            tickFormatter={(v) => `${Math.round(v / 10000).toLocaleString()}`}
                            tick={{ fontSize: 12 }}
                            width={56}
                        />
                        <Tooltip formatter={(v, name) => [formatMan(v), name]} />
                        <Legend />
                        <Bar dataKey="collected" name="수납" fill={MAIN} radius={[3, 3, 0, 0]} />
                        <Bar dataKey="refunded" name="환불" fill={ACCENT} radius={[3, 3, 0, 0]} />
                    </BarChart>
                </ResponsiveContainer>
            </div>
            <p className="mt-2 text-right text-xs text-text/50">단위: 만원</p>
        </section>
    )
}

export default CollectionChart