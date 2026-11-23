package com.coliving.platform.safety.controller;

import com.coliving.platform.safety.dto.BlockListResponseDTO;
import com.coliving.platform.safety.dto.BlockRequestDTO;
import com.coliving.platform.safety.dto.BlockResponseDTO;
import com.coliving.platform.safety.service.BlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 차단 관리 (API 8-2 ~ 8-4)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/blocks")
@RequiredArgsConstructor
public class BlockController {

	private final BlockService blockService;

	/**
	 * 내 차단 목록을 조회한다.
	 * 입주자가 차단한 회원을 관리할 때 사용한다. 회원 표시 정보와 차단 일시를 제공한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 차단 회원 목록
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_RESIDENT 입주자 아님,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public BlockListResponseDTO getBlocks(@RequestHeader("X-User") Long userId) {
		return blockService.getBlocks(userId);
	}

	/**
	 * 회원을 차단한다.
	 * 입주자가 다른 회원의 글과 채팅을 숨기려 할 때 사용한다. 본인과 관리자는 차단할 수 없다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 차단할 회원 ID
	 * @return 201 차단 대상 ID와 차단 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 CANNOT_BLOCK_SELF 본인 차단 불가,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         404 USER_NOT_FOUND 회원 없음,
	 *         409 ALREADY_BLOCKED 이미 차단함,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping
	public ResponseEntity<BlockResponseDTO> block(
			@RequestHeader("X-User") Long userId,
			@Valid @RequestBody BlockRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(blockService.block(userId, request.blockedUserId()));
	}

	/**
	 * 회원 차단을 해제한다.
	 * 회원이 기존 차단을 취소할 때 사용한다. 차단 중 숨겨진 채팅 메시지는 다시 표시하지 않는다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param blockedUserId 차단 대상 회원 ID (예: 5002)
	 * @return 204 차단 해제 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 404 BLOCK_NOT_FOUND 차단 내역 없음
	 */
	@DeleteMapping("/{blockedUserId}")
	public ResponseEntity<Void> unblock(@RequestHeader("X-User") Long userId, @PathVariable Long blockedUserId) {
		blockService.unblock(userId, blockedUserId);
		return ResponseEntity.noContent().build();
	}
}
