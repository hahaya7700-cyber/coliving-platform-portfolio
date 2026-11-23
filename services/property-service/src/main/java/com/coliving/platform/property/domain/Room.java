package com.coliving.platform.property.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 호실. 삭제는 행을 지우지 않고 표시만 남긴다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(nullable = false, length = 50)
    private String roomType;

    @Column(length = 100)
    private String roomStructure;

    /** MONTHLY / SHORT_TERM / OTHER */
    @Column(name = "rental_type", nullable = false, length = 30)
    private String leaseType;

    @Column(nullable = false)
    private long monthlyFee;

    @Column(nullable = false)
    private long deposit;

    @Column(nullable = false)
    private long managementFee;

    @Column(name = "floor_number", nullable = false)
    private Integer floor;

    @Column(name = "area", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaM2;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomFacility> facilities = new ArrayList<>();

    /** JPA가 쓰는 기본 생성자. */
    protected Room() {}

    /** 소속 건물만 받아 호실을 만든다. 나머지 값은 update에서 채운다. */
    public Room(Building building) {
        this.building = building;
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

    /** 호실 정보를 고친다. */
    public void update(Building building, String roomName, String roomType, String roomStructure, String leaseType,
                       long monthlyFee, long deposit, long managementFee,
                       Integer floor, BigDecimal areaM2, Integer capacity) {
        this.building = building;
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomStructure = roomStructure;
        this.leaseType = leaseType;
        this.monthlyFee = monthlyFee;
        this.deposit = deposit;
        this.managementFee = managementFee;
        this.floor = floor;
        this.areaM2 = areaM2;
        this.capacity = capacity;
    }

    /** 편의시설을 비운다. */
    public void clearFacilities() {
        facilities.clear();
    }

    /** 편의시설을 이 호실에 붙인다. */
    public void addFacility(RoomFacility f) {
        f.setRoom(this);
        facilities.add(f);
    }

    /** 소프트 삭제 표시를 한다. */
    public void markDeleted() { this.deleted = true; }

    /** 호실 번호. */
    public Long getId() { return id; }
    /** 소속 건물. */
    public Building getBuilding() { return building; }
    /** 호실 이름. */
    public String getRoomName() { return roomName; }
    /** 방 종류. */
    public String getRoomType() { return roomType; }
    /** 방 구조. */
    public String getRoomStructure() { return roomStructure; }
    /** 임대 유형. MONTHLY, SHORT_TERM, OTHER. */
    public String getLeaseType() { return leaseType; }
    /** 월 이용료. */
    public long getMonthlyFee() { return monthlyFee; }
    /** 보증금. */
    public long getDeposit() { return deposit; }
    /** 관리비. */
    public long getManagementFee() { return managementFee; }
    /** 층. */
    public Integer getFloor() { return floor; }
    /** 전용 면적(㎡). */
    public BigDecimal getAreaM2() { return areaM2; }
    /** 수용 인원. */
    public Integer getCapacity() { return capacity; }
    /** 삭제 여부. */
    public boolean isDeleted() { return deleted; }
    /** 호실 편의시설. */
    public List<RoomFacility> getFacilities() { return facilities; }
}
