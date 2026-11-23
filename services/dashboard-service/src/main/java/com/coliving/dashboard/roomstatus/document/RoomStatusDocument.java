package com.coliving.dashboard.roomstatus.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDate;

/**
 * 호실 현황 문서 (호실당 1건만 저장하고, 상태가 바뀌면 같은 ID로 덮어씀)
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "room-status", createIndex = false)
public class RoomStatusDocument {

    /* 호실 현황 ID = room_id */
    @Id
    private String id;

    /* 호실 ID */
    @Field(name = "room_id", type = FieldType.Long)
    private Long roomId;

    /* 건물 ID */
    @Field(name = "building_id", type = FieldType.Long)
    private Long buildingId;

    /* 건물명 */
    @Field(name = "building_name", type = FieldType.Keyword)
    private String buildingName;

    /* 층 */
    @Field(type = FieldType.Integer)
    private Integer floor;

    /* 호실명 */
    @Field(name = "room_name", type = FieldType.Keyword)
    private String roomName;

    /* 저장된 호실 상태 (화면·집계에는 날짜를 반영한 RoomStatusMapper.resolve 값 사용): OCCUPIED(입주) / VACANT(공실) / MOVING_OUT(퇴거 예정) / APPLYING(신청 진행 중) / MOVE_IN_SCHEDULED(입주 예정) */
    @Field(type = FieldType.Keyword)
    private String status;

    /* 이 상태를 만든 계약 ID (옛 데이터는 null) */
    @Field(name = "application_id", type = FieldType.Long)
    private Long applicationId;

    /* 입주일 (옛 데이터는 null) */
    @Field(name = "move_in_date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate moveInDate;

    /* 퇴실 예정일 (옛 데이터는 null) */
    @Field(name = "move_out_date", type = FieldType.Date, format = DateFormat.date)
    private LocalDate moveOutDate;

    /* 이 상태를 만든 계약의 보증금 (옛 데이터는 null) */
    @Field(type = FieldType.Long)
    private Long deposit;

    /* 퇴실로 보증금 보관액에서 이미 뺐는지 (뺐으면 true) */
    @Field(name = "deposit_released", type = FieldType.Boolean)
    private Boolean depositReleased;

    /* 상태 변경 시간 */
    @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.date_time)
    private Instant updatedAt;
}