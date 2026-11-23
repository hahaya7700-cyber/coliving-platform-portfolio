package com.coliving.dashboard.property;

import java.util.List;

/**
 * 매물 서비스가 내려준 건물 하나의 호실 목록. 입주 상태는 포함하지 않는다
 * @author 조휘재
 * @version 1.0
 * @since 2026-11-12
 */
public record BuildingRoomList(long buildingId, String buildingName, List<Room> rooms) {

    /* 호실 목록이 null이면 빈 목록으로, 아니면 복사해 변경 불가로 저장 */
    public BuildingRoomList {
        rooms = rooms == null ? List.of() : List.copyOf(rooms);
    }

    /* 호실 하나 (호실 ID, 층, 호실명) */
    public record Room(long roomId, int floor, String roomName) {
    }
}
