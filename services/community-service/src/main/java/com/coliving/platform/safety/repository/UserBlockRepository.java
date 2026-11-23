package com.coliving.platform.safety.repository;

import com.coliving.platform.safety.entity.UserBlockEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 회원 차단의 저장·조회 접근을 제공한다.
 *
 * @author 오시문
 */
public interface UserBlockRepository extends JpaRepository<UserBlockEntity, Long> {

	List<UserBlockEntity> findByBlockerIdOrderByCreatedAtDesc(Long blockerId);

	Optional<UserBlockEntity> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

	@Query("select b.blockedId from UserBlockEntity b where b.blockerId = :blockerId")
	List<Long> findBlockedIds(@Param("blockerId") Long blockerId);
}
