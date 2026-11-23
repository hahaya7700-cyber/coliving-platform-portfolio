package com.coliving.platform.note.controller;

import com.coliving.platform.note.dto.NoteCreateRequestDTO;
import com.coliving.platform.note.dto.NoteCreateResponseDTO;
import com.coliving.platform.note.dto.NoteListResponseDTO;
import com.coliving.platform.note.service.NoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 쪽지 — 사용자 (API 1-1 ~ 1-3)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/notes")
@RequiredArgsConstructor
public class NoteController {

	private final NoteService noteService;

	/**
	 * 내 쪽지 목록을 조회한다.
	 * 회원이 매니저와 주고받은 쪽지를 확인할 때 사용한다. 최신 쪽지부터 페이지로 반환한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 30, 1 이상, 100 이하
	 * @return 200 쪽지 목록 페이지와 안 읽은 수
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public NoteListResponseDTO getMyNotes(
			@RequestHeader("X-User") Long userId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "30") @Min(1) @Max(100) int size) {
		return noteService.getMyNotes(userId, page, size);
	}

	/**
	 * 매니저에게 쪽지를 보낸다.
	 * 회원이 문의할 때 사용한다. 쪽지함을 재사용하거나 새로 만들고 관리자에게 알린다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 쪽지 내용 (공백만 입력 불가, 1~1000자)
	 * @return 201 생성된 쪽지 ID와 생성 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 404 USER_NOT_FOUND 회원 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping
	public ResponseEntity<NoteCreateResponseDTO> send(
			@RequestHeader("X-User") Long userId,
			@Valid @RequestBody NoteCreateRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(noteService.send(userId, request.content()));
	}

	/**
	 * 받은 쪽지를 모두 읽음 처리한다.
	 * 회원이 쪽지 화면을 확인했을 때 사용한다. 관리자에게 받은 미확인 쪽지를 읽음 처리한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 204 읽음 처리 완료, 응답 본문 없음
	 */
	@PatchMapping("/read")
	public ResponseEntity<Void> readAll(@RequestHeader("X-User") Long userId) {
		noteService.readAll(userId);
		return ResponseEntity.noContent().build();
	}
}
