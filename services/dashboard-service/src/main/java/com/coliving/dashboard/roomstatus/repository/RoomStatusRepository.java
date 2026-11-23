package com.coliving.dashboard.roomstatus.repository;

import com.coliving.dashboard.roomstatus.document.RoomStatusDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 호실 현황 저장·조회
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public interface RoomStatusRepository extends ElasticsearchRepository<RoomStatusDocument, String> {

    /* 건물별 호실 현황을 조회한다 */
    List<RoomStatusDocument> findByBuildingId(Long buildingId, Pageable pageable);
}