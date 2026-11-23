package com.coliving.platform.property.domain;

import jakarta.persistence.*;

/**
 * 건물·언어별 계약서 양식. 한 건물에 언어는 하나씩이고, 언어는 ko 또는 en 이다.
 *
 * @author 김정호
 * 작성일: 2026-10-27
 */
@Entity
@Table(name = "building_contract_forms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"building_id", "language"}))
public class BuildingContractForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(name = "form_id", nullable = false)
    private Long formId;

    /** JPA가 쓰는 기본 생성자. */
    protected BuildingContractForm() {}

    /** 건물·언어·양식 번호로 연결을 만든다. */
    public BuildingContractForm(Long buildingId, String language, Long formId) {
        this.buildingId = buildingId;
        this.language = language;
        this.formId = formId;
    }

    /** 건물 번호. */
    public Long getBuildingId() { return buildingId; }
    /** 양식 언어. ko 또는 en. */
    public String getLanguage() { return language; }
    /** 문서 양식 번호. */
    public Long getFormId() { return formId; }
}
