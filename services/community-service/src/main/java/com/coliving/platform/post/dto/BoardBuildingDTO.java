package com.coliving.platform.post.dto;

/**
 * 관리자 게시판 건물 선택 목록 API의 응답 항목.
 * 관리자 게시판 건물 선택 목록 항목
 *
 * @param buildingId 건물 ID (예: 1)
 * @param name 건물 이름 (예: 강남점)
 * @author 오시문
 */
public record BoardBuildingDTO(Long buildingId, String name) {
}
