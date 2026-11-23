package com.coliving.platform.faq.repository;

import com.coliving.platform.faq.entity.FaqIndexJobEntity;
import com.coliving.platform.faq.entity.FaqIndexStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * FAQ 반영 작업의 저장·조회 접근을 제공한다.
 *
 * @author 오시문
 */
public interface FaqIndexJobRepository extends JpaRepository<FaqIndexJobEntity, Long> {

	boolean existsByStatus(FaqIndexStatus status);

	List<FaqIndexJobEntity> findByStatus(FaqIndexStatus status);
}
