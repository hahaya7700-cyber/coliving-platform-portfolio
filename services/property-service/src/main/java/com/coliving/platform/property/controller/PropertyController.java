package com.coliving.platform.property.controller;

import com.coliving.platform.property.dto.PropertyDtos.*;
import com.coliving.platform.property.service.PropertyQueryService;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 매물 조회 API. 비회원도 호출할 수 있고, 게이트웨이가 /property-service 를 제거해 /property/** 로 전달한다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@RestController
@RequestMapping("/property")
public class PropertyController {

    private final PropertyQueryService service;

    /** 조회 서비스를 주입받는다. */
    public PropertyController(PropertyQueryService service) {
        this.service = service;
    }

    /** 건물 검색. 키워드·지역·월세·보증금·방 종류로 거르고, 조건이 없으면 전체를 본다. */
    @GetMapping("/buildings")
    public PageResponse<BuildingSummary> search(@RequestParam(name = "keyword", required = false) String keyword,
                                                @RequestParam(name = "region", required = false) String region,
                                                @RequestParam(name = "minMonthlyFee", required = false) Long minMonthlyFee,
                                                @RequestParam(name = "maxMonthlyFee", required = false) Long maxMonthlyFee,
                                                @RequestParam(name = "maxDeposit", required = false) Long maxDeposit,
                                                @RequestParam(name = "roomType", required = false) String roomType,
                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "12") int size) {
        return service.search(new BuildingSearchCondition(keyword, region, minMonthlyFee, maxMonthlyFee, maxDeposit,
                roomType), page, size);
    }

    /** 건물 상세. 없거나 삭제됐으면 404. */
    @GetMapping("/buildings/{id}")
    public BuildingDetail building(@PathVariable("id") Long id) {
        return service.getBuilding(id);
    }

    /** 호실 상세. 없거나 삭제됐으면 404. */
    @GetMapping("/rooms/{id}")
    public RoomDetail room(@PathVariable("id") Long id) {
        return service.getRoom(id);
    }
}
