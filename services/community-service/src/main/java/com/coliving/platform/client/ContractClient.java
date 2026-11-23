package com.coliving.platform.client;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

/**
 * contract-service 호출 (연동 요청 API 2-1)
 *
 * @author 오시문
 */
public interface ContractClient {

	/** 입주 건물·호실 일괄 조회 — 계약 없는 회원은 결과에서 빠짐 */
	Map<Long, ResidentRoom> getResidentRooms(Collection<Long> userIds);

	/**
	 * 계약 서비스에서 조회한 회원의 입주 건물·호실 정보.
	 *
	 * @param userId 회원 ID (예: 5001)
	 * @param contractId 계약 ID (예: 100)
	 * @param buildingId 건물 ID (예: 1)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @param roomNumber 호실 (예: 302호)
	 * @param contractStatus 계약 서비스가 제공한 계약 상태 문자열
	 * @param moveInDate 실제 입주일 (예: 2026-11-01)
	 * @param contractEndDate 계약 종료일 (예: 2026-12-31)
	 * @author 오시문
	 */
	record ResidentRoom(Long userId, Long contractId, Long buildingId, String buildingName, String roomNumber,
			String contractStatus, LocalDate moveInDate, LocalDate contractEndDate) {
	}
}
