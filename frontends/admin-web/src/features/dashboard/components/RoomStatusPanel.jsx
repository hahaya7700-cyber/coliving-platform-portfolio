/**
 * @file 호실 현황 패널 (층별 호실 타일과 상태별 범례)
 * @author 문경완
 * @since 2026-11-12
 * @version 1.0.0
 */

/* 호실 상태별 표시 라벨과 타일 스타일 */
const STATUS = {
    APPLYING: {
        label: '신청 진행 중',
        tile: 'border-2 border-dashed border-main bg-text/10 text-main',
    },
    VACANT: {
        label: '공실',
        tile: 'bg-text/10 text-text/60',
    },
    OCCUPIED: {
        label: '입주',
        tile: 'bg-main text-background',
    },
    MOVE_IN_SCHEDULED: {
        label: '입주 예정',
        tile: 'border-2 border-main bg-main/15 text-main',
    },
    MOVING_OUT: {
        label: '퇴거 예정',
        tile: 'bg-accent text-text',
    },
}

/**
 * 호실 목록을 층별로 묶음
 * @param {Array} rooms - 호실 목록
 * @returns {Array} [층, 호실 목록] 쌍의 배열
 */
function groupByFloor(rooms) {
    const byFloor = new Map()

    for (const room of rooms) {
        const key = room.floor ?? '층 미정'
        if (!byFloor.has(key)) byFloor.set(key, [])
        byFloor.get(key).push(room)
    }

    return Array.from(byFloor.entries())
}

/**
 * 상태별 호실 수 집계 (모르는 상태는 공실로 셈)
 * @param {Array} rooms - 호실 목록
 * @returns {Object} 상태별 호실 수
 */
function countByStatus(rooms) {
    const counts = {}
    for (const room of rooms) {
        const key = STATUS[room.status] ? room.status : 'VACANT'
        counts[key] = (counts[key] ?? 0) + 1
    }
    return counts
}

/**
 * 호실 현황 패널
 * @param {Object} props
 * @param {Array} props.rooms - 호실 목록 ({roomId, roomName, floor, status})
 * @returns {JSX.Element}
 */
function RoomStatusPanel({rooms}) {
    const floors = groupByFloor(rooms)
    const counts = countByStatus(rooms)

    // 층마다 호실 수가 달라도 열 폭이 같도록, 가장 호실이 많은 층 기준으로 열 수를 정한다.
    const columns = Math.max(1, ...floors.map(([, list]) => list.length))
    const gridStyle = {gridTemplateColumns: `repeat(${columns}, minmax(0, 1fr))`}

    return (
        <section className="flex flex-col rounded-lg border border-main/20 bg-white p-4">
            <h2 className="mb-3 text-base font-semibold text-text">호실 현황</h2>

            {rooms.length === 0 ? (
                <p className="flex flex-1 items-center justify-center py-8 text-sm text-text/50">
                    등록된 호실이 없습니다.
                </p>
            ) : (
                <div className="flex w-full flex-1 flex-col justify-center gap-2 px-2">
                    {floors.map(([floor, list]) => (
                        <div key={floor} className="flex w-full items-center gap-2">
                            <span className="w-7 shrink-0 whitespace-nowrap text-left text-sm text-text/70">
                                {typeof floor === 'number' ? `${floor}층` : floor}
                            </span>

                            <div className="grid min-w-0 flex-1 gap-2" style={gridStyle}>
                                {list.map((room) => {
                                    const style = STATUS[room.status] ?? STATUS.VACANT

                                    return (
                                        <div
                                            key={room.roomId}
                                            title={`${room.roomName} · ${style.label}`}
                                            className={`box-border flex h-10 min-w-0 items-center justify-center truncate rounded-md px-1 text-sm font-semibold ${style.tile}`}
                                        >
                                            {room.roomName}
                                        </div>
                                    )
                                })}
                            </div>
                        </div>
                    ))}
                </div>
            )}

            <ul className="mt-3 flex flex-wrap justify-center gap-x-5 gap-y-1 border-t border-main/10 pt-3 text-xs text-text/70">
                {Object.entries(STATUS).map(([key, {label, tile}]) => (
                    <li key={key} className="flex items-center gap-1.5">
                        <span className={`inline-block h-3 w-3 rounded-sm ${tile}`}/>
                        {label}
                        <span className="font-semibold text-text">{counts[key] ?? 0}</span>
                    </li>
                ))}
            </ul>
        </section>
    )
}

export default RoomStatusPanel