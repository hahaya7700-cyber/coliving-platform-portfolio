package com.coliving.platform.property.repository;

import com.coliving.platform.property.domain.Room;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 호실 저장소. 조회는 삭제되지 않은 호실만 고른다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
    /** 삭제되지 않은 호실을 번호로 찾는다. */
    Optional<Room> findByIdAndDeletedFalse(Long id);

    /** 건물의 삭제되지 않은 호실을 이름순으로 찾는다. */
    List<Room> findByBuildingIdAndDeletedFalseOrderByRoomName(Long buildingId);

    /** 여러 건물의 삭제되지 않은 호실을 찾는다. */
    List<Room> findByBuildingIdInAndDeletedFalse(Collection<Long> buildingIds);
}
