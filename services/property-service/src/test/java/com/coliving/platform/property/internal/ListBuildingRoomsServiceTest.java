package com.coliving.platform.property.internal;

import com.coliving.platform.grpc.property.ListBuildingRoomsResponse;
import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.domain.Room;
import com.coliving.platform.property.repository.BuildingRepository;
import com.coliving.platform.property.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListBuildingRoomsServiceTest {

    private final BuildingRepository buildings = mock(BuildingRepository.class);
    private final RoomRepository rooms = mock(RoomRepository.class);
    private final ListBuildingRoomsService service = new ListBuildingRoomsService(buildings, rooms);

    /** 삭제되지 않은 건물의 이름과 호실을 돌려주는지 확인한다. */
    @Test
    void sendsBuildingNameAndRoomsThatAreNotDeleted() {
        Building building = building(1L, "성수 A동");
        when(buildings.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(building));
        when(rooms.findByBuildingIdAndDeletedFalseOrderByRoomName(1L)).thenReturn(List.of(
                room(building, 301L, "301호", 3),
                room(building, 102L, "102호", 1)
        ));

        ListBuildingRoomsResponse response = service.listBuildingRooms(1L).orElseThrow();

        assertThat(response.getBuildingId()).isEqualTo(1L);
        assertThat(response.getBuildingName()).isEqualTo("성수 A동");
        assertThat(response.getRoomsList()).extracting(com.coliving.platform.grpc.property.Room::getRoomId)
                .containsExactly(301L, 102L);
        assertThat(response.getRoomsList()).extracting(com.coliving.platform.grpc.property.Room::getRoomName)
                .containsExactly("301호", "102호");
        assertThat(response.getRoomsList()).extracting(com.coliving.platform.grpc.property.Room::getFloor)
                .containsExactly(3, 1);
        verify(rooms).findByBuildingIdAndDeletedFalseOrderByRoomName(1L);
    }

    /** 없거나 삭제된 건물은 빈 결과를 돌려주는지 확인한다. */
    @Test
    void missingOrDeletedBuildingIsEmpty() {
        when(buildings.findByIdAndDeletedFalse(9L)).thenReturn(Optional.empty());

        assertThat(service.listBuildingRooms(9L)).isEmpty();
    }

    /** 테스트용 건물을 만든다. */
    private static Building building(long id, String name) {
        Building building = new Building(name, "주소");
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }

    /** 테스트용 호실을 만든다. */
    private static Room room(Building building, long id, String name, int floor) {
        Room room = new Room(building);
        room.update(building, name, "원룸", null, "MONTHLY", 0, 0, 0, floor, BigDecimal.ONE, 1);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }
}
