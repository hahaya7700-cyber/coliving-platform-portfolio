package com.coliving.platform.property.domain;

import jakarta.persistence.*;

/**
 * 호실 편의시설. 종류와 이름을 호실 아래에 둔다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Entity
@Table(name = "room_facilities")
public class RoomFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "facility_type", nullable = false, length = 50)
    private String facilityType;

    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;

    /** JPA가 쓰는 기본 생성자. */
    protected RoomFacility() {}

    /** 편의시설 종류와 이름으로 만든다. */
    public RoomFacility(String facilityType, String facilityName) {
        this.facilityType = facilityType;
        this.facilityName = facilityName;
    }

    /** 소속 호실을 붙인다. */
    void setRoom(Room room) { this.room = room; }

    /** 편의시설 종류. */
    public String getFacilityType() { return facilityType; }
    /** 편의시설 이름. */
    public String getFacilityName() { return facilityName; }
}
