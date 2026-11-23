package com.coliving.platform.faq.controller;

import com.coliving.platform.common.auth.ResidentGuard;
import com.coliving.platform.faq.dto.FaqDocumentListResponseDTO;
import com.coliving.platform.faq.dto.FaqDocumentUploadResponseDTO;
import com.coliving.platform.faq.dto.FaqIndexJobDTO;
import com.coliving.platform.faq.dto.FaqReindexRequestDTO;
import com.coliving.platform.faq.entity.FaqDocumentEntity;
import com.coliving.platform.faq.entity.FaqScope;
import com.coliving.platform.faq.service.AdminFaqDocumentService;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FAQ 문서 관리 — 관리자 (API 7-2 ~ 7-7)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/admin/faq-documents")
@RequiredArgsConstructor
public class AdminFaqDocumentController {

	private final AdminFaqDocumentService adminFaqDocumentService;
	private final ResidentGuard residentGuard;

	/**
	 * FAQ 문서와 반영 상태를 조회한다.
	 * 관리자가 문서를 관리할 때 사용한다. 범위와 건물로 문서 목록을 좁힐 수 있다.
	 * 7-2 문서 목록 · 반영 상태
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param scope FAQ 문서 적용 범위 (COMMON / BUILDING 중 하나), 생략 가능
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @return 200 문서 목록과 전체 반영 현황
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public FaqDocumentListResponseDTO getDocuments(@RequestHeader("X-User") Long userId,
			@RequestParam(required = false) FaqScope scope, @RequestParam(required = false) Long buildingId) {
		residentGuard.requireAdmin(userId);
		return adminFaqDocumentService.getDocuments(scope, buildingId);
	}

	/**
	 * FAQ 원본 문서를 업로드한다.
	 * 관리자가 FAQ 근거 문서를 등록할 때 사용한다. 업로드 후 별도로 업데이트 반영을 시작해야 한다.
	 * 7-3 업로드 (multipart/form-data)
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param file 업로드할 원본 파일 (PDF / DOCX / TXT / MD, 비어 있지 않은 최대 30MB)
	 * @param title 문서 제목 (1~100자)
	 * @param scope FAQ 문서 적용 범위 (COMMON / BUILDING 중 하나)
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @return 201 생성된 문서 ID와 반영 대기 상태
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 ADMIN_ONLY 관리자 전용,
	 *         413 FILE_TOO_LARGE 30MB 초과,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<FaqDocumentUploadResponseDTO> upload(@RequestHeader("X-User") Long userId,
			@RequestParam("file") MultipartFile file, @RequestParam("title") String title,
			@RequestParam("scope") FaqScope scope, @RequestParam(value = "buildingId", required = false) Long buildingId) {
		residentGuard.requireAdmin(userId);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(adminFaqDocumentService.upload(userId, file, title, scope, buildingId));
	}

	/**
	 * FAQ 문서를 삭제한다.
	 * 관리자가 사용하지 않는 문서를 제거할 때 사용한다. 원본과 검색 조각을 삭제하고 답변 캐시를 갱신한다.
	 * 7-4 삭제
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param documentId FAQ 문서 ID (예: 1)
	 * @return 204 삭제 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         404 DOCUMENT_NOT_FOUND 문서 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@DeleteMapping("/{documentId}")
	public ResponseEntity<Void> delete(@RequestHeader("X-User") Long userId, @PathVariable Long documentId) {
		residentGuard.requireAdmin(userId);
		adminFaqDocumentService.delete(documentId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * FAQ 원본 파일을 다운로드한다.
	 * 관리자가 등록된 문서 원본을 확인할 때 사용한다. 첨부 파일 이름과 형식을 함께 반환한다.
	 * 7-5 원본 다운로드
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param documentId FAQ 문서 ID (예: 1)
	 * @return 200 원본 파일 바이트
	 * @throws com.coliving.platform.common.exception.CommunityException 403 ADMIN_ONLY 관리자 전용,
	 *         404 DOCUMENT_NOT_FOUND 문서 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/{documentId}/file")
	public ResponseEntity<byte[]> download(@RequestHeader("X-User") Long userId, @PathVariable Long documentId) {
		residentGuard.requireAdmin(userId);
		FaqDocumentEntity document = adminFaqDocumentService.getFile(documentId);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
						.filename(document.getFileName(), StandardCharsets.UTF_8).build().toString())
				.contentType(MediaType.parseMediaType(document.getFileType().contentType()))
				.contentLength(document.getFileData().length)
				.body(document.getFileData());
	}

	/**
	 * FAQ 문서의 업데이트 반영을 시작한다.
	 * 관리자가 업로드·수정된 문서를 봇에 적용할 때 사용한다. 작업을 만든 뒤 실제 반영은 커밋 후 비동기로 진행한다.
	 * 7-6 업데이트 반영 시작 — Body 생략 가능 (대기·실패 문서 전체)
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 반영할 문서 ID 목록 (본문 생략·빈 목록이면 대기·실패 문서 전체)
	 * @return 202 생성된 반영 작업과 진행 상태
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 ADMIN_ONLY 관리자 전용,
	 *         404 DOCUMENT_NOT_FOUND 문서 없음,
	 *         409 REINDEX_IN_PROGRESS 문서 반영 중,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/reindex")
	public ResponseEntity<FaqIndexJobDTO> reindex(@RequestHeader("X-User") Long userId,
			@RequestBody(required = false) FaqReindexRequestDTO request) {
		residentGuard.requireAdmin(userId);
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(adminFaqDocumentService.startReindex(userId, request != null ? request.documentIds() : null));
	}

	/**
	 * FAQ 문서 반영 작업의 진행 상태를 조회한다.
	 * 관리자가 반영 시작 후 진행률과 실패 사유를 확인할 때 사용한다.
	 * 7-7 반영 진행 상태
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param jobId 문서 반영 작업 ID (예: 1)
	 * @return 200 작업 상태와 처리 문서 수
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 ADMIN_ONLY 관리자 전용,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/reindex/{jobId}")
	public FaqIndexJobDTO getJob(@RequestHeader("X-User") Long userId, @PathVariable Long jobId) {
		residentGuard.requireAdmin(userId);
		return adminFaqDocumentService.getJob(jobId);
	}
}
