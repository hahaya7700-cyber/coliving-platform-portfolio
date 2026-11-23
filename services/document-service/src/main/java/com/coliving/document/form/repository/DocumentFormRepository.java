package com.coliving.document.form.repository;

import com.coliving.document.form.entity.DocumentForm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * document_forms 테이블을 읽고 쓰는 저장소
 * @author 문경완
 * @version 1.0
 * @since 2026-11-12
 */
public interface DocumentFormRepository extends JpaRepository<DocumentForm, Long> {

    /* 그 언어(ko, en)의 양식 개수 */
    long countByLanguage(String language);
}