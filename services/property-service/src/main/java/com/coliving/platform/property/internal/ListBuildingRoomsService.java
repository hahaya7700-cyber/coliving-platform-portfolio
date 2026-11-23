package com.coliving.platform.property.internal;

import com.coliving.platform.grpc.property.ListBuildingRoomsResponse;
import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.domain.Room;
import com.coliving.platform.property.repository.BuildingRepository;
import com.coliving.platform.property.repository.RoomRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대시보드용 건물 호실 목록. 삭제된 건물·호실은 빼서 보낸다.
 *
 * @author 김정호
 * 작성일: 2026-11-04
 */
@Service
@Transactional(readOnly = true)
public class ListBuildingRoomsService {

    private final BuildingRepository buildings;
    private final RoomRepository rooms;

    /** 건물·호실 저장소를 주입받는다. */
    public ListBuildingRoomsService(BuildingRepository buildings, RoomRepository rooms) {
        this.buildings = buildings;
        this.rooms = rooms;
    }

    /** 삭제되지 않은 건물의 호실 목록. 건물이 없으면 빈 값. */
    public Optional<ListBuildingRoomsResponse> listBuildingRooms(long buildingId) {
        return buildings.findByIdAndDeletedFalse(buildingId).map(this::toResponse);
    }

    /** 건물과 삭제되지 않은 호실을 gRPC 응답으로 옮긴다. 호실은 이름순. */
    private ListBuildingRoomsResponse toResponse(Building building) {
        ListBuildingRoomsResponse.Builder response = ListBuildingRoomsResponse.newBuilder()
                .setBuildingId(building.getId())
                .setBuildingName(building.getName() == null ? "" : building.getName());
        for (Room room : rooms.findByBuildingIdAndDeletedFalseOrderByRoomName(building.getId())) {
            response.addRooms(toRoom(room));
        }
        return response.build();
    }

    /** 호실을 gRPC 호실로 옮긴다. 번호·층·이름이 없으면 0 또는 빈 문자열. */
    private static com.coliving.platform.grpc.property.Room toRoom(Room room) {
        int floor = room.getFloor() == null ? 0 : room.getFloor();
        String name = room.getRoomName() == null ? "" : room.getRoomName();
        long roomId = room.getId() == null ? 0L : room.getId();
        return com.coliving.platform.grpc.property.Room.newBuilder()
                .setRoomId(roomId)
                .setFloor(floor)
                .setRoomName(name)
                .build();
    }
}
