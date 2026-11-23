package com.coliving.platform.client;

import java.util.List;

/**
 * property-service 호출 (연동 요청 API 3-1)
 *
 * @author 오시문
 */
public interface PropertyClient {

	List<BuildingSummary> getBuildings();

	default String getBuildingName(Long buildingId) {
		if (buildingId == null) {
			return null;
		}
		return getBuildings().stream()
				.filter(building -> building.buildingId().equals(buildingId))
				.map(BuildingSummary::name)
				.findFirst()
				.orElse(null);
	}

	/**
	 * 건물 서비스에서 조회한 건물 ID와 표시 이름.
	 *
	 * @param buildingId 건물 ID (예: 1)
	 * @param name 이름 (예: 김민서)
	 * @author 오시문
	 */
	record BuildingSummary(Long buildingId, String name) {
	}
}
