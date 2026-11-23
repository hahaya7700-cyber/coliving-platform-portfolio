package com.coliving.platform.property.repository;

import com.coliving.platform.property.domain.Building;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 건물 저장소. 목록 검색은 Specification 으로 거른다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
public interface BuildingRepository extends JpaRepository<Building, Long>, JpaSpecificationExecutor<Building> {
    /** 삭제되지 않은 건물을 번호로 찾는다. */
    Optional<Building> findByIdAndDeletedFalse(Long id);

    /** 삭제되지 않은 건물을 번호 오름차순으로 모두 찾는다. */
    List<Building> findAllByDeletedFalseOrderByIdAsc();
}
