/**
 * @file 건물·조회 월 선택 필터
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

import { monthText, recentMonths } from '../utils/buildings'

/* 조회 월 선택 목록 (최근 6개월) */
const MONTHS = recentMonths(6)

/* 선택 상자 공통 스타일 */
const selectClass =
    'rounded-md border-2 border-main bg-white px-3 py-1.5 text-sm text-text focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-accent'

/**
 * 건물·조회 월 선택 필터
 * @param {Object} props
 * @param {Array} props.buildings - 건물 목록 ({id, name})
 * @param {number|null} props.buildingId - 선택된 건물 ID
 * @param {string} props.month - 선택된 조회 월 (yyyy-MM)
 * @param {Function} props.onBuildingChange - 건물이 바뀔 때 호출
 * @param {Function} props.onMonthChange - 월이 바뀔 때 호출
 * @returns {JSX.Element}
 */
function FilterBar({ buildings, buildingId, month, onBuildingChange, onMonthChange }) {
    return (
        <div className="flex gap-2">
            <select
                aria-label="건물 선택"
                className={selectClass}
                value={buildingId ?? ''}
                disabled={buildings.length === 0}
                onChange={(e) => onBuildingChange(Number(e.target.value))}
            >
                {buildings.length === 0 ? (
                    <option value="">등록된 건물이 없습니다</option>
                ) : (
                    buildings.map((b) => (
                        <option key={b.id} value={b.id}>
                            {b.name}
                        </option>
                    ))
                )}
            </select>
            <select
                aria-label="조회 월 선택"
                className={selectClass}
                value={month}
                onChange={(e) => onMonthChange(e.target.value)}
            >
                {MONTHS.map((m) => (
                    <option key={m} value={m}>
                        {monthText(m)}
                    </option>
                ))}
            </select>
        </div>
    )
}

export default FilterBar