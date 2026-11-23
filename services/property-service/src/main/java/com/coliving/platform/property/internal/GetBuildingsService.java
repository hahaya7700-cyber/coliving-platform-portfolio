package com.coliving.platform.property.internal;

import com.coliving.platform.grpc.property.BuildingSummary;
import com.coliving.platform.grpc.property.GetBuildingsResponse;
import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.repository.BuildingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 커뮤니티용 건물 목록. is_deleted 인 건물은 빼서 보낸다.
 *
 * @author 김정호
 * 작성일: 2026-11-04
 */
@Service
@Transactional(readOnly = true)
public class GetBuildingsService {

    private final BuildingRepository buildings;

    /** 건물 저장소를 주입받는다. */
    public GetBuildingsService(BuildingRepository buildings) {
        this.buildings = buildings;
    }

    /** 삭제되지 않은 건물을 번호 오름차순으로 돌려준다. */
    public GetBuildingsResponse getBuildings() {
        GetBuildingsResponse.Builder response = GetBuildingsResponse.newBuilder();
        for (Building building : buildings.findAllByDeletedFalseOrderByIdAsc()) {
            response.addBuildings(toSummary(building));
        }
        return response.build();
    }

    /** 건물을 gRPC 요약으로 옮긴다. 이름이 없으면 빈 문자열. */
    private static BuildingSummary toSummary(Building building) {
        String name = building.getName() == null ? "" : building.getName();
        return BuildingSummary.newBuilder()
                .setBuildingId(building.getId())
                .setBuildingName(name)
                .build();
    }
}
