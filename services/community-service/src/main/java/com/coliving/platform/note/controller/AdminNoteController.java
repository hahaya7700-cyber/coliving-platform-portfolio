package com.coliving.platform.note.controller;

import com.coliving.platform.common.dto.PageResponse;
import com.coliving.platform.note.dto.AdminNoteCreateRequestDTO;
import com.coliving.platform.note.dto.AdminNoteCreateResponseDTO;
import com.coliving.platform.note.dto.AdminReplyResponseDTO;
import com.coliving.platform.note.dto.AdminThreadDetailDTO;
import com.coliving.platform.note.dto.AdminThreadListItemDTO;
import com.coliving.platform.note.dto.AdminThreadUpdateRequestDTO;
import com.coliving.platform.note.dto.AdminThreadUpdateResponseDTO;
import com.coliving.platform.note.dto.NoteCreateRequestDTO;
import com.coliving.platform.note.entity.NoteThreadStatus;
import com.coliving.platform.note.entity.NoteThreadType;
import com.coliving.platform.note.service.AdminNoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 쪽지함 — 관리자 (API 3-1 ~ 3-5)
 * TODO: 관리자 권한 확인 (Gateway가 넘겨주는 역할 정보 확정 후)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/admin")
@RequiredArgsConstructor
public class AdminNoteController {

	private final AdminNoteService adminNoteService;

	/**
	 * 관리자 쪽지함 목록을 조회한다.
	 * 관리자가 회원·비회원 문의를 확인할 때 사용한다. 유형·답변 상태·검색어로 조회한다.
	 *
	 * @param type 항목 유형 (CANDIDATE / RESIDENT / MEMBER / GUEST 중 하나), 생략 가능
	 * @param status 현재 처리 상태 (UNANSWERED / ANSWERED 중 하나), 생략 가능
	 * @param keyword 검색어 (예: 공용 공간)
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20, 1 이상, 100 이하
	 * @return 200 쪽지함 목록 페이지
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/note-threads")
	public PageResponse<AdminThreadListItemDTO> getThreads(
			@RequestParam(required = false) NoteThreadType type,
			@RequestParam(required = false) NoteThreadStatus status,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		return adminNoteService.getThreads(type, status, keyword, page, size);
	}

	/**
	 * 관리자 쪽지함 상세를 조회한다.
	 * 관리자가 문의 내용과 이전 답변을 확인할 때 사용한다. 조회 시 회원이 보낸 쪽지를 읽음 처리한다.
	 *
	 * @param threadId 쪽지함 ID (예: 1)
	 * @return 200 문의자 정보와 쪽지 내역
	 * @throws com.coliving.platform.common.exception.CommunityException 404 THREAD_NOT_FOUND 쪽지함 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/note-threads/{threadId}")
	public AdminThreadDetailDTO getThread(@PathVariable Long threadId) {
		return adminNoteService.getThread(threadId);
	}

	/**
	 * 회원 문의에 답장한다.
	 * 관리자가 회원 쪽지함에서 답변할 때 사용한다. 비회원 문의에는 이 경로로 답장할 수 없다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param threadId 쪽지함 ID (예: 1)
	 * @param request 쪽지 내용 (공백만 입력 불가, 1~1000자)
	 * @return 201 생성된 답장 ID와 쪽지함 상태
	 * @throws com.coliving.platform.common.exception.CommunityException 400 GUEST_REPLY_NOT_ALLOWED 비회원 쪽지 답장 불가,
	 *         404 THREAD_NOT_FOUND 쪽지함 없음
	 */
	@PostMapping("/note-threads/{threadId}/notes")
	public ResponseEntity<AdminReplyResponseDTO> reply(
			@RequestHeader("X-User") Long adminId,
			@PathVariable Long threadId,
			@Valid @RequestBody NoteCreateRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminNoteService.reply(adminId, threadId, request.content()));
	}

	/**
	 * 관리자 쪽지함 상태와 메모를 수정한다.
	 * 관리자가 문의 처리 상태나 내부 메모를 정리할 때 사용한다. 요청에 포함된 값만 반영한다.
	 *
	 * @param threadId 쪽지함 ID (예: 1)
	 * @param request 변경할 답변 상태와 내부 메모 (1000자 이하, null 필드는 유지)
	 * @return 200 수정된 쪽지함 상태와 메모
	 * @throws com.coliving.platform.common.exception.CommunityException 404 THREAD_NOT_FOUND 쪽지함 없음
	 */
	@PatchMapping("/note-threads/{threadId}")
	public AdminThreadUpdateResponseDTO update(
			@PathVariable Long threadId,
			@Valid @RequestBody AdminThreadUpdateRequestDTO request) {
		return adminNoteService.update(threadId, request);
	}

	/**
	 * 관리자가 회원에게 새 쪽지를 보낸다.
	 * 관리자가 특정 회원에게 먼저 안내할 때 사용한다. 회원 쪽지함을 재사용하거나 새로 만든다.
	 *
	 * @param adminId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 수신 회원 ID와 쪽지 내용 (1~1000자)
	 * @return 201 생성된 쪽지함·쪽지 ID
	 * @throws com.coliving.platform.common.exception.CommunityException 404 USER_NOT_FOUND 회원 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/notes")
	public ResponseEntity<AdminNoteCreateResponseDTO> create(
			@RequestHeader("X-User") Long adminId,
			@Valid @RequestBody AdminNoteCreateRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminNoteService.create(adminId, request));
	}
}
