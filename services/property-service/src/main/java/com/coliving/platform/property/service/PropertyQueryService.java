package com.coliving.platform.property.service;

import com.coliving.platform.property.client.OccupancyClient;
import com.coliving.platform.property.domain.Building;
import com.coliving.platform.property.domain.Room;
import com.coliving.platform.property.dto.PropertyDtos.*;
import com.coliving.platform.property.repository.BuildingRepository;
import com.coliving.platform.property.repository.RoomRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 사용자 매물 조회. 비회원도 건물 검색, 건물 상세, 호실 상세를 볼 수 있다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Service
@Transactional(readOnly = true)
public class PropertyQueryService {

    private final BuildingRepository buildings;
    private final RoomRepository rooms;
    private final OccupancyClient occupancy;

    /** 건물·호실 저장소와 입주 여부 클라이언트를 주입받는다. */
    public PropertyQueryService(BuildingRepository buildings, RoomRepository rooms, OccupancyClient occupancy) {
        this.buildings = buildings;
        this.rooms = rooms;
        this.occupancy = occupancy;
    }

    /** 조건에 맞는 건물 목록. 최저 월세·보증금과 입주 가능 호실 수를 같이 계산한다. */
    public PageResponse<BuildingSummary> search(BuildingSearchCondition cond, int page, int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by("id").descending());
        Page<Building> result = buildings.findAll(toSpec(cond), pageable);

        List<Long> ids = result.getContent().stream().map(Building::getId).toList();
        Map<Long, List<Room>> roomsByBuilding = ids.isEmpty()
                ? Map.of()
                : rooms.findByBuildingIdInAndDeletedFalse(ids).stream()
                .collect(Collectors.groupingBy(r -> r.getBuilding().getId()));

        return PageResponse.of(result.map(b -> {
            List<Room> list = roomsByBuilding.getOrDefault(b.getId(), List.of());
            Long minFee = list.stream().map(Room::getMonthlyFee).min(Long::compare).orElse(null);
            Long minDeposit = list.stream().map(Room::getDeposit).min(Long::compare).orElse(null);
            return new BuildingSummary(b.getId(), b.getName(), b.getAddress(), minFee, minDeposit,
                    availableCount(list), b.getImageUrl());
        }));
    }

    /** 건물 상세와 삭제되지 않은 호실. 없으면 404. */
    public BuildingDetail getBuilding(Long id) {
        Building b = buildings.findByIdAndDeletedFalse(id).orElseThrow(PropertyQueryService::notFound);
        List<Room> list = rooms.findByBuildingIdAndDeletedFalseOrderByRoomName(id);
        Map<Long, Boolean> availability = occupancy.getRoomAvailability(list.stream().map(Room::getId).toList());
        return new BuildingDetail(b.getId(), b.getName(), b.getAddress(), b.getRegion(), b.getBuildingType(), b.getTotalFloors(),
                b.isParkingAvailable(), b.isElevatorAvailable(), b.getIntroduction(), b.getContactPhone(),
                b.getImageUrl(), PropertyMapper.facilities(b),
                list.stream().map(r -> PropertyMapper.roomSummary(r, availability)).toList());
    }

    /** 호실 상세. 호실이나 소속 건물이 삭제됐으면 404. 대표 사진은 아직 없어 null. */
    public RoomDetail getRoom(Long id) {
        Room r = rooms.findByIdAndDeletedFalse(id).orElseThrow(PropertyQueryService::notFound);
        Building b = r.getBuilding();
        if (b.isDeleted()) throw notFound();
        return new RoomDetail(r.getId(), b.getId(), b.getName(), b.getAddress(), null,
                r.getRoomName(), r.getRoomType(),
                r.getRoomStructure(), r.getLeaseType(), r.getMonthlyFee(), r.getDeposit(), r.getManagementFee(),
                r.getFloor(), r.getAreaM2(), r.getCapacity(), occupancy.isAvailable(r.getId()).orElse(null),
                b.getBuildingType(), b.isParkingAvailable(), b.isElevatorAvailable(), b.getContactPhone(),
                b.getImageUrl(), PropertyMapper.roomFacilities(r));
    }

    /** 입주 가능한 호실 수. 계약 서비스에 연결하지 못하면 null. */
    private Integer availableCount(List<Room> list) {
        Map<Long, Boolean> availability = occupancy.getRoomAvailability(list.stream().map(Room::getId).toList());
        if (availability.isEmpty()) return null; // 연동 전
        return (int) availability.values().stream().filter(Boolean::booleanValue).count();
    }

    /** 검색 조건을 건물 조회 조건으로 옮긴다. 월세·보증금·방 종류는 호실이 하나라도 맞으면 포함한다. */
    private static Specification<Building> toSpec(BuildingSearchCondition c) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.isFalse(root.get("deleted")));

            if (hasText(c.keyword())) {
                String like = "%" + c.keyword().trim().toLowerCase() + "%";
                p.add(cb.or(cb.like(cb.lower(root.get("name")), like), cb.like(cb.lower(root.get("address")), like),
                        cb.like(cb.lower(root.get("region")), like)));
            }
            if (hasText(c.region())) {
                p.add(cb.like(cb.lower(root.get("region")), "%" + c.region().trim().toLowerCase() + "%"));
            }

            boolean roomFilter = c.minMonthlyFee() != null || c.maxMonthlyFee() != null || c.maxDeposit() != null
                    || hasText(c.roomType());
            if (roomFilter) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Room> room = sub.from(Room.class);
                List<Predicate> rp = new ArrayList<>();
                rp.add(cb.equal(room.get("building"), root));
                rp.add(cb.isFalse(room.get("deleted")));
                if (c.minMonthlyFee() != null) rp.add(cb.greaterThanOrEqualTo(room.get("monthlyFee"), c.minMonthlyFee()));
                if (c.maxMonthlyFee() != null) rp.add(cb.lessThanOrEqualTo(room.get("monthlyFee"), c.maxMonthlyFee()));
                if (c.maxDeposit() != null) rp.add(cb.lessThanOrEqualTo(room.get("deposit"), c.maxDeposit()));
                if (hasText(c.roomType())) rp.add(cb.equal(room.get("roomType"), c.roomType().trim()));
                sub.select(room.<Long>get("id")).where(rp.toArray(Predicate[]::new));
                p.add(cb.exists(sub));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }

    /** null이 아니고 공백만 있는 문자열도 아니면 true. */
    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    /** 매물을 찾지 못했을 때의 404. */
    static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "매물을 찾을 수 없습니다.");
    }
}