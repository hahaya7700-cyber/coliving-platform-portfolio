package com.coliving.platform.property.internal;

import com.coliving.platform.grpc.property.BuildingSummary;
import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.repository.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetBuildingsServiceTest {

    private final BuildingRepository buildings = mock(BuildingRepository.class);
    private final GetBuildingsService service = new GetBuildingsService(buildings);

    /** 삭제되지 않은 건물만 번호 오름차순으로 돌려주는지 확인한다. */
    @Test
    void sendsOnlyBuildingsFromTheNotDeletedQuery() {
        when(buildings.findAllByDeletedFalseOrderByIdAsc()).thenReturn(List.of(
                building(1L, "망원 스테이"),
                building(2L, "성수 하우스")
        ));

        List<BuildingSummary> result = service.getBuildings().getBuildingsList();

        assertThat(result).extracting(BuildingSummary::getBuildingId).containsExactly(1L, 2L);
        assertThat(result).extracting(BuildingSummary::getBuildingName).containsExactly("망원 스테이", "성수 하우스");
        verify(buildings).findAllByDeletedFalseOrderByIdAsc();
    }

    /** 테스트용 건물을 만든다. */
    private static Building building(long id, String name) {
        Building building = new Building(name, "주소");
        ReflectionTestUtils.setField(building, "id", id);
        return building;
    }
}
