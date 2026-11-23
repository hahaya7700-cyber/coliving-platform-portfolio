package com.coliving.platform.property.repository;

import com.coliving.platform.property.domain.BuildingContractForm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 건물 계약서 양식 저장소.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
public interface BuildingContractFormRepository extends JpaRepository<BuildingContractForm, Long> {
    /** 건물에 연결된 계약서 양식을 찾는다. */
    List<BuildingContractForm> findByBuildingId(Long buildingId);

    /** 건물에 연결된 계약서 양식을 모두 지운다. */
    void deleteByBuildingId(Long buildingId);
}
