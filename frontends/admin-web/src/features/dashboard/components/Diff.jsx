/**
 * @file 증감 표시 컴포넌트
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/**
 * 지난달·지난주 대비 증감 표시 (값이 없으면 "비교 데이터 없음")
 * @param {Object} props
 * @param {number|null} props.value - 증감 값
 * @param {string} props.unit - 값 뒤에 붙일 단위
 * @param {string} props.label - 비교 기준 문구
 * @param {boolean} props.goodWhenUp - 값이 올라가면 좋은 지표인지 (기본 true)
 * @returns {JSX.Element}
 */
function Diff({ value, unit, label, goodWhenUp = true }) {
    if (value == null) return <p className="mt-1 text-xs text-text/50">{label} 비교 데이터 없음</p>
    if (value === 0) return <p className="mt-1 text-xs text-text/70">변동 없음 · {label}</p>

    const up = value > 0
    const good = up === goodWhenUp
    return (
        <p className={`mt-1 text-xs ${good ? 'text-main' : 'text-red-600'}`}>
            {up ? '▲' : '▼'} {Math.abs(value)}
            {unit} <span className="text-text/50">{label}</span>
        </p>
    )
}

export default Diff