/**
 * @file 입주율 추이 꺾은선 그래프 (최근 6개월)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'

/* 주 색상 (입주율 선) */
const MAIN = '#52796F'

/**
 * yyyy-MM 에서 월 숫자만 뽑아 "10월" 형태로 변환
 * @param {string} m - yyyy-MM 형식의 월
 * @returns {string} 월 라벨
 */
const monthLabel = (m) => `${Number(m.slice(5))}월`

/**
 * 입주율 추이 꺾은선 그래프
 * @param {Object} props
 * @param {Array} props.trend - 최근 6개월 추이 (month, occupancyRate)
 * @returns {JSX.Element}
 */
function OccupancyTrendChart({ trend }) {
    const data = trend.map((t) => ({ name: monthLabel(t.month), rate: t.occupancyRate }))

    return (
        <section className="rounded-lg border border-main/20 bg-white p-5">
            <div className="mb-3 flex items-baseline justify-between">
                <h2 className="text-base font-semibold text-text">입주율 추이</h2>
                <span className="text-xs text-text/50">최근 6개월</span>
            </div>
            <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                        <YAxis
                            domain={['dataMin - 3', 'dataMax + 3']}
                            tickFormatter={(v) => `${Math.round(v)}%`}
                            tick={{ fontSize: 12 }}
                            width={44}
                        />
                        <Tooltip formatter={(v) => [`${v}%`, '입주율']} />
                        <Line type="monotone" dataKey="rate" stroke={MAIN} strokeWidth={2} dot={{ r: 4 }} connectNulls />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </section>
    )
}

export default OccupancyTrendChart