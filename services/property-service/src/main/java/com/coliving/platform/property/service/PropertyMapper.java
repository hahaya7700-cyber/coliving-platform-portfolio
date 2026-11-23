package com.coliving.platform.property.service;

import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.domain.Room;
import com.coliving.platform.property.dto.PropertyDtos.FacilityDto;
import com.coliving.platform.property.dto.PropertyDtos.RoomSummary;
import java.util.List;
import java.util.Map;

/**
 * 건물·호실을 조회 응답으로 옮긴다. 인스턴스는 만들지 않는다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
final class PropertyMapper {

    /** 인스턴스를 만들지 않는다. */
    private PropertyMapper() {}

    /** 건물 편의시설을 응답 틀로 옮긴다. */
    static List<FacilityDto> facilities(Building b) {
        return b.getFacilities().stream()
                .map(f -> new FacilityDto(f.getFacilityType(), f.getFacilityName()))
                .toList();
    }

    /** 호실 편의시설을 응답 틀로 옮긴다. */
    static List<FacilityDto> roomFacilities(Room r) {
        return r.getFacilities().stream()
                .map(f -> new FacilityDto(f.getFacilityType(), f.getFacilityName()))
                .toList();
    }

    /** 호실을 목록 한 줄로 옮긴다. 입주 가능 여부는 availability에서 꺼낸다. */
    static RoomSummary roomSummary(Room r, Map<Long, Boolean> availability) {
        return new RoomSummary(r.getId(), r.getRoomName(), r.getRoomType(), r.getLeaseType(), r.getMonthlyFee(),
                r.getDeposit(), r.getManagementFee(), r.getFloor(), r.getAreaM2(), r.getCapacity(),
                availability.get(r.getId()));
    }
}
