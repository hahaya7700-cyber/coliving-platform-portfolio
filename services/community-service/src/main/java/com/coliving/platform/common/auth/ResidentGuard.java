package com.coliving.platform.common.auth;

import com.coliving.platform.client.ContractClient;
import com.coliving.platform.client.ContractClient.ResidentRoom;
import com.coliving.platform.client.UserClient;
import com.coliving.platform.client.UserClient.UserSummary;
import com.coliving.platform.common.exception.CommunityException;
import com.coliving.platform.common.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 입주자 확인 — 커뮤니티 게시판·채팅은 실제 입주한 사람(roles 에 RESIDENT)만, 같은 건물만
 * 건물은 요청으로 받지 않고 계약 서비스 정보로 정함 (API 명세서 0-1)
 *
 * @author 오시문
 */
@Component
@RequiredArgsConstructor
public class ResidentGuard {

	private final UserClient userClient;
	private final ContractClient contractClient;

	/**
	 * 입주자가 아니면 403 NOT_RESIDENT
	 *
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_RESIDENT 입주자 아님,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	public Resident requireResident(Long userId) {
		UserSummary user = userClient.getUser(userId);
		if (user == null || !user.isResident()) {
			throw new CommunityException(ErrorCode.NOT_RESIDENT);
		}
		ResidentRoom room = contractClient.getResidentRooms(List.of(userId)).get(userId);
		if (room == null) {
			throw new CommunityException(ErrorCode.NOT_RESIDENT);
		}
		return new Resident(userId, user.name(), room.buildingId(), room.buildingName(), room.roomNumber());
	}

	/** 관리자 여부 (TODO: Gateway가 역할 정보를 넘겨주면 그 값으로 교체) */
	public boolean isAdmin(Long userId) {
		UserSummary user = userClient.getUser(userId);
		return user != null && user.isAdmin();
	}

	/**
	 * 관리자가 아니면 403 ADMIN_ONLY
	 *
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	public void requireAdmin(Long userId) {
		if (!isAdmin(userId)) {
			throw new CommunityException(ErrorCode.ADMIN_ONLY);
		}
	}

	/**
	 * 같은 건물이 아니면 403 OTHER_BUILDING
	 *
	 * @throws com.coliving.platform.common.exception.CommunityException 403 OTHER_BUILDING 다른 건물 접근
	 */
	public void requireSameBuilding(Resident resident, Long buildingId) {
		if (!resident.buildingId().equals(buildingId)) {
			throw new CommunityException(ErrorCode.OTHER_BUILDING);
		}
	}

	/**
	 * 입주자 권한 확인을 통과한 회원의 거주 정보.
	 *
	 * @param userId 회원 ID (예: 5001)
	 * @param name 이름 (예: 김민서)
	 * @param buildingId 건물 ID (예: 1)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @param roomNumber 호실 (예: 302호)
	 * @author 오시문
	 */
	public record Resident(Long userId, String name, Long buildingId, String buildingName, String roomNumber) {
	}
}
