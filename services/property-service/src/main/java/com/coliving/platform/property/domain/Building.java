package com.coliving.platform.property.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 건물. 삭제는 행을 지우지 않고 표시만 남긴다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "building_name", nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(nullable = false, length = 50)
    private String buildingType;

    @Column(nullable = false)
    private Integer totalFloors;

    private boolean parkingAvailable;
    private boolean elevatorAvailable;

    @Column(name = "description", columnDefinition = "TEXT")
    private String introduction;

    @Column(length = 30)
    private String contactPhone;

    // 대표 이미지 공개 URL (예: /property-service/property/images/{파일명})
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuildingFacility> facilities = new ArrayList<>();

    /** JPA가 쓰는 기본 생성자. */
    protected Building() {}

    /** 이름과 주소만 받아 건물을 만든다. 나머지 값은 update에서 채운다. */
    public Building(String name, String address) {
        this.name = name;
        this.address = address;
    }

    /** 처음 저장할 때 등록 시각과 수정 시각을 채운다. */
    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    /** 수정할 때 수정 시각을 갱신한다. */
    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    /** 건물 정보를 고친다. */
    public void update(String name, String address, String region, String buildingType, Integer totalFloors,
                       boolean parkingAvailable, boolean elevatorAvailable,
                       String introduction, String contactPhone) {
        this.name = name;
        this.address = address;
        this.region = region;
        this.buildingType = buildingType;
        this.totalFloors = totalFloors;
        this.parkingAvailable = parkingAvailable;
        this.elevatorAvailable = elevatorAvailable;
        this.introduction = introduction;
        this.contactPhone = contactPhone;
    }

    /** 편의시설을 비운다. */
    public void clearFacilities() {
        facilities.clear();
    }

    /** 편의시설을 이 건물에 붙인다. */
    public void addFacility(BuildingFacility f) {
        f.setBuilding(this);
        facilities.add(f);
    }

    /** 대표 이미지 주소를 바꾼다. null 이면 비운다. */
    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /** 소프트 삭제 표시를 한다. */
    public void markDeleted() {
        this.deleted = true;
    }

    /** 건물 번호. */
    public Long getId() { return id; }
    /** 건물명. */
    public String getName() { return name; }
    /** 주소. */
    public String getAddress() { return address; }
    /** 지역. */
    public String getRegion() { return region; }
    /** 건물 유형. */
    public String getBuildingType() { return buildingType; }
    /** 총 층수. */
    public Integer getTotalFloors() { return totalFloors; }
    /** 주차 가능 여부. */
    public boolean isParkingAvailable() { return parkingAvailable; }
    /** 엘리베이터 여부. */
    public boolean isElevatorAvailable() { return elevatorAvailable; }
    /** 소개글. */
    public String getIntroduction() { return introduction; }
    /** 연락처. */
    public String getContactPhone() { return contactPhone; }
    /** 대표 이미지 주소. */
    public String getImageUrl() { return imageUrl; }
    /** 삭제 여부. */
    public boolean isDeleted() { return deleted; }
    /** 마지막 수정 시각. */
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    /** 건물 편의시설. */
    public List<BuildingFacility> getFacilities() { return facilities; }
}