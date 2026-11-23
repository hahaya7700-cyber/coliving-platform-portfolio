package com.coliving.platform.property.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * 매물 서비스 요청·응답. 바깥 클래스는 인스턴스를 만들지 않는다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
public final class PropertyDtos {

    /** 인스턴스를 만들지 않는다. */
    private PropertyDtos() {}

    /**
     * 목록 한 페이지.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
        /** 스프링 페이지를 응답 틀로 옮긴다. */
        public static <T> PageResponse<T> of(Page<T> p) {
            return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        }
    }

    /**
     * 편의시설. JSON 필드는 화면의 category, name 과 같다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record FacilityDto(@NotBlank @Size(max = 30) String category, @NotBlank @Size(max = 100) String name) {}

    // ── 사용자 ────────────────────────────────────────
    /**
     * 사용자 건물 검색 조건. 비어 있는 값은 거르지 않는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record BuildingSearchCondition(String keyword, String region, Long minMonthlyFee, Long maxMonthlyFee,
                                          Long maxDeposit, String roomType) {}

    /**
     * 검색 목록의 건물 한 줄. 최저 월세·보증금과 입주 가능 호실 수를 담는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record BuildingSummary(Long id, String name, String address, Long lowestMonthlyFee, Long lowestDeposit,
                                  Integer availableRoomCount, String imageUrl) {}

    /**
     * 건물 상세의 호실 한 줄. available 이 null 이면 입주 여부를 아직 모른다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record RoomSummary(Long id, String roomName, String roomType, String leaseType, long monthlyFee,
                              long deposit, long managementFee, Integer floor, BigDecimal areaM2, Integer capacity,
                              Boolean available) {}

    /**
     * 사용자 건물 상세. 편의시설과 호실 목록을 같이 담는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record BuildingDetail(Long id, String name, String address, String region, String buildingType, Integer totalFloors,
                                 boolean parkingAvailable, boolean elevatorAvailable, String introduction,
                                 String contactPhone, String imageUrl, List<FacilityDto> facilities,
                                 List<RoomSummary> rooms) {}

    /**
     * 사용자 호실 상세. 호실 사진 저장이 없어 thumbnailUrl 은 null 이다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record RoomDetail(Long id, Long buildingId, String buildingName, String address,
                             /** 대표 사진. 호실 사진 저장이 없어 현재는 null. */
                             String thumbnailUrl,
                             String roomName, String roomType,
                             String roomStructure, String leaseType, long monthlyFee, long deposit,
                             long managementFee, Integer floor, BigDecimal areaM2, Integer capacity,
                             Boolean available, String buildingType, boolean parkingAvailable,
                             boolean elevatorAvailable, String contactPhone, String buildingImageUrl,
                             List<FacilityDto> facilities) {}

    // ── 관리자 ────────────────────────────────────────
    /**
     * 관리자 건물 목록 한 줄. 입주 여부를 모르면 availableRooms 는 null 이다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record AdminBuildingSummary(Long id, String name, String address, int totalRooms, Integer availableRooms,
                                       OffsetDateTime updatedAt) {}

    /**
     * 관리자 건물 상세의 호실 한 줄.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record AdminRoomSummary(Long id, String roomName, String roomType, long monthlyFee, long deposit,
                                   long managementFee, Integer floor) {}

    /**
     * 건물 언어별 계약서 양식. language 는 ko 또는 en 이다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record ContractFormDto(@NotBlank @Pattern(regexp = "ko|en") String language, @NotNull @Positive Long formId) {}

    /**
     * 관리자 건물 상세. 계약서 양식과 호실 목록을 같이 담는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record AdminBuildingDetail(Long id, String name, String address, String region, String buildingType, Integer totalFloors,
                                      boolean parkingAvailable, boolean elevatorAvailable, String introduction,
                                      String contactPhone, String imageUrl, List<FacilityDto> facilities,
                                      List<ContractFormDto> contractForms, List<AdminRoomSummary> rooms) {}

    /**
     * 건물 등록·수정 본문. 계약서 양식은 이 본문에 넣지 않는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record BuildingSaveRequest(@NotBlank @Size(max = 200) String name,
                                      @NotBlank @Size(max = 500) String address,
                                      @NotBlank @Size(max = 100) String region,
                                      @NotBlank @Size(max = 50) String buildingType,
                                      @NotNull @Positive Integer totalFloors,
                                      boolean parkingAvailable,
                                      boolean elevatorAvailable,
                                      String introduction,
                                      @Size(max = 30) String contactPhone,
                                      @Valid List<FacilityDto> facilities) {}

    /**
     * 건물 계약서 양식 교체 본문. 요청에 없는 언어는 지운다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record ContractFormsRequest(@Valid @NotNull List<ContractFormDto> forms) {}

    /**
     * 관리자 호실 상세.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record AdminRoomDetail(Long id, Long buildingId, String roomName, String roomType, String roomStructure,
                                  String leaseType, long monthlyFee, long deposit, long managementFee, Integer floor,
                                  BigDecimal areaM2, Integer capacity, List<FacilityDto> facilities) {}

    /**
     * 호실 등록·수정 본문. leaseType 은 MONTHLY, SHORT_TERM, OTHER 만 받는다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record RoomSaveRequest(@NotNull Long buildingId,
                                  @NotBlank @Size(max = 100) String roomName,
                                  @NotBlank @Size(max = 50) String roomType,
                                  @Size(max = 100) String roomStructure,
                                  /** MONTHLY(월세) / SHORT_TERM(단기) / OTHER(기타) */
                                  @NotBlank @Pattern(regexp = "MONTHLY|SHORT_TERM|OTHER") String leaseType,
                                  @PositiveOrZero long monthlyFee,
                                  @PositiveOrZero long deposit,
                                  @PositiveOrZero long managementFee,
                                  @NotNull Integer floor,
                                  @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal areaM2,
                                  /** 비우면 1 */
                                  @Positive @Max(32767) Integer capacity,
                                  @Valid List<FacilityDto> facilities) {}

    /**
     * 등록 결과로 돌려주는 번호.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record IdResponse(Long id) {}

    /**
     * 이미지 업로드 결과. 화면은 게이트웨이 주소 뒤에 이 공개 URL 을 붙여 쓴다.
     *
     * @author 김정호
     * 작성일: 2026-10-27
     */
    public record ImageResponse(String imageUrl) {}
}