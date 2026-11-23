package com.coliving.platform.note.repository;

import com.coliving.platform.note.entity.GuestInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 비회원 문의의 저장·조회 접근을 제공한다.
 *
 * @author 오시문
 */
public interface GuestInquiryRepository extends JpaRepository<GuestInquiryEntity, Long> {
}
