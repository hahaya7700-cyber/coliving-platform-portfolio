package com.coliving.platform.property.service;

import com.coliving.platform.property.client.OccupancyClient;
import com.coliving.platform.property.domain.*;
import com.coliving.platform.property.dto.PropertyDtos.*;
import com.coliving.platform.property.repository.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * 관리자 매물 저장. 건물·호실 등록과 수정, 소프트 삭제, 계약서 양식, 대표 이미지를 다룬다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Service
@Transactional
public class PropertyAdminService {

    private final BuildingRepository buildings;
    private final RoomRepository rooms;
    private final BuildingContractFormRepository contractForms;
    private final OccupancyClient occupancy;
    private final BuildingImageStorage imageStorage;

    /** 건물·호실 저장소, 계약서 양식 저장소, 입주 여부 클라이언트를 주입받는다. */
    public PropertyAdminService(BuildingRepository buildings, RoomRepository rooms,
                                BuildingContractFormRepository contractForms, OccupancyClient occupancy,
                                BuildingImageStorage imageStorage) {
        this.buildings = buildings;
        this.rooms = rooms;
        this.contractForms = contractForms;
        this.occupancy = occupancy;
        this.imageStorage = imageStorage;
    }

    /** 삭제되지 않은 건물 목록. 호실 수와 입주 가능 호실 수를 같이 센다. 입주 여부를 모르면 가능 수는 null. */
    @Transactional(readOnly = true)
    public PageResponse<AdminBuildingSummary> list(int page, int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("id").descending());
        Specification<Building> notDeleted = (r, q, cb) -> cb.isFalse(r.get("deleted"));
        var result = buildings.findAll(notDeleted, pageable);

        List<Long> ids = result.getContent().stream().map(Building::getId).toList();
        Map<Long, List<Room>> byBuilding = new HashMap<>();
        if (!ids.isEmpty()) {
            rooms.findByBuildingIdInAndDeletedFalse(ids)
                    .forEach(r -> byBuilding.computeIfAbsent(r.getBuilding().getId(), k -> new ArrayList<>()).add(r));
        }
        return PageResponse.of(result.map(b -> {
            List<Room> list = byBuilding.getOrDefault(b.getId(), List.of());
            Map<Long, Boolean> availability = occupancy.getRoomAvailability(list.stream().map(Room::getId).toList());
            Integer available = availability.isEmpty()
                    ? null
                    : (int) availability.values().stream().filter(Boolean::booleanValue).count();
            return new AdminBuildingSummary(b.getId(), b.getName(), b.getAddress(), list.size(), available,
                    b.getUpdatedAt());
        }));
    }

    /** 건물 상세. 편의시설, 계약서 양식, 삭제되지 않은 호실을 함께 돌려준다. 없으면 404. */
    @Transactional(readOnly = true)
    public AdminBuildingDetail getBuilding(Long id) {
        Building b = findBuilding(id);
        return new AdminBuildingDetail(b.getId(), b.getName(), b.getAddress(), b.getRegion(), b.getBuildingType(),
                b.getTotalFloors(), b.isParkingAvailable(), b.isElevatorAvailable(), b.getIntroduction(),
                b.getContactPhone(), b.getImageUrl(), PropertyMapper.facilities(b),
                contractForms.findByBuildingId(id).stream()
                        .map(f -> new ContractFormDto(f.getLanguage(), f.getFormId())).toList(),
                rooms.findByBuildingIdAndDeletedFalseOrderByRoomName(id).stream()
                        .map(r -> new AdminRoomSummary(r.getId(), r.getRoomName(), r.getRoomType(), r.getMonthlyFee(),
                                r.getDeposit(), r.getManagementFee(), r.getFloor()))
                        .toList());
    }

    /** 건물을 등록하고 새 번호를 돌려준다. */
    public IdResponse createBuilding(BuildingSaveRequest req) {
        Building b = new Building(req.name().trim(), req.address().trim());
        apply(b, req);
        return new IdResponse(buildings.save(b).getId());
    }

    /** 건물 정보를 수정한다. 없으면 404. */
    public void updateBuilding(Long id, BuildingSaveRequest req) {
        apply(findBuilding(id), req);
    }

    /** 소프트 삭제: 계약 이력이 호실을 참조하므로 행을 지우지 않는다. 소속 호실도 함께 숨김. */
    public void deleteBuilding(Long id) {
        Building b = findBuilding(id);
        b.markDeleted();
        rooms.findByBuildingIdAndDeletedFalseOrderByRoomName(id).forEach(Room::markDeleted);
    }

    /** 요청에 없는 언어는 제거, 있는 언어는 덮어쓴다. */
    public void saveContractForms(Long buildingId, ContractFormsRequest req) {
        findBuilding(buildingId);
        Set<String> languages = new HashSet<>();
        req.forms().forEach(f -> {
            if (!languages.add(f.language())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "언어가 중복되었습니다: " + f.language());
            }
        });
        contractForms.deleteByBuildingId(buildingId);
        contractForms.flush();
        req.forms().forEach(f -> contractForms.save(new BuildingContractForm(buildingId, f.language(), f.formId())));
    }

    /** 대표 이미지 등록·교체: 파일을 저장하고 공개 URL 을 건물에 기록한다. 이전 파일은 지운다. */
    public ImageResponse uploadBuildingImage(Long buildingId, MultipartFile file) {
        Building b = findBuilding(buildingId);
        String oldUrl = b.getImageUrl();
        String url = imageStorage.store(file);
        b.changeImageUrl(url);
        imageStorage.deleteByUrl(oldUrl);
        return new ImageResponse(url);
    }

    /** 대표 이미지 삭제 */
    public void deleteBuildingImage(Long buildingId) {
        Building b = findBuilding(buildingId);
        String oldUrl = b.getImageUrl();
        b.changeImageUrl(null);
        imageStorage.deleteByUrl(oldUrl);
    }

    /** 호실 상세. 없으면 404. */
    @Transactional(readOnly = true)
    public AdminRoomDetail getRoom(Long id) {
        Room r = findRoom(id);
        return new AdminRoomDetail(r.getId(), r.getBuilding().getId(), r.getRoomName(), r.getRoomType(),
                r.getRoomStructure(), r.getLeaseType(), r.getMonthlyFee(), r.getDeposit(), r.getManagementFee(),
                r.getFloor(), r.getAreaM2(), r.getCapacity(), PropertyMapper.roomFacilities(r));
    }

    /** 호실을 등록하고 새 번호를 돌려준다. 건물이 없으면 404. */
    public IdResponse createRoom(RoomSaveRequest req) {
        Room r = new Room(findBuilding(req.buildingId()));
        apply(r, req);
        return new IdResponse(rooms.save(r).getId());
    }

    /** 호실 정보를 수정한다. 없으면 404. */
    public void updateRoom(Long id, RoomSaveRequest req) {
        apply(findRoom(id), req);
    }

    /** 호실을 소프트 삭제한다. 없으면 404. */
    // TODO: 진행 중 계약이 있는 호실의 삭제 허용 여부는 계약 서비스와 확인 후 막는다.
    public void deleteRoom(Long id) {
        findRoom(id).markDeleted();
    }

    /** 요청 값으로 건물과 편의시설을 덮어쓴다. 같은 편의시설 제약이 있어 기존 행을 먼저 비운다. */
    private void apply(Building b, BuildingSaveRequest req) {
        b.update(req.name().trim(), req.address().trim(), req.region().trim(), req.buildingType().trim(),
                req.totalFloors(), req.parkingAvailable(), req.elevatorAvailable(),
                blankToNull(req.introduction()), blankToNull(req.contactPhone()));
        // (building_id, type, name) UNIQUE 제약: 기존 행 삭제를 먼저 반영(flush)한 뒤 다시 넣는다.
        b.clearFacilities();
        if (b.getId() != null) buildings.flush();
        distinct(req.facilities()).forEach(f -> b.addFacility(new BuildingFacility(f.category(), f.name())));
    }

    /** 요청 값으로 호실과 편의시설을 덮어쓴다. 면적은 소수 둘째 자리로 맞춘다. */
    private void apply(Room r, RoomSaveRequest req) {
        Building b = findBuilding(req.buildingId());
        BigDecimal area = req.areaM2().setScale(2, java.math.RoundingMode.HALF_UP);
        r.update(b, req.roomName().trim(), req.roomType().trim(), blankToNull(req.roomStructure()),
                req.leaseType(), req.monthlyFee(), req.deposit(), req.managementFee(), req.floor(), area,
                req.capacity() == null ? 1 : req.capacity());
        r.clearFacilities();
        if (r.getId() != null) rooms.flush();
        distinct(req.facilities()).forEach(f -> r.addFacility(new RoomFacility(f.category(), f.name())));
    }

    /** 삭제되지 않은 건물을 찾는다. 없으면 404. */
    private Building findBuilding(Long id) {
        return buildings.findByIdAndDeletedFalse(id).orElseThrow(PropertyQueryService::notFound);
    }

    /** 삭제되지 않은 호실을 찾는다. 없으면 404. */
    private Room findRoom(Long id) {
        return rooms.findByIdAndDeletedFalse(id).orElseThrow(PropertyQueryService::notFound);
    }

    /** 편의시설 종류·이름의 앞뒤 공백을 지우고 중복을 뺀다. 목록이 없으면 빈 목록. */
    private static List<FacilityDto> distinct(List<FacilityDto> list) {
        if (list == null) return List.of();
        return list.stream().map(f -> new FacilityDto(f.category().trim(), f.name().trim())).distinct().toList();
    }

    /** 비어 있거나 공백만 있으면 null, 아니면 앞뒤 공백을 뺀 문자열. */
    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}