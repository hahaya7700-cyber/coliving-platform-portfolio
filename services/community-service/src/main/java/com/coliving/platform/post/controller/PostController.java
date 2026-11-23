package com.coliving.platform.post.controller;

import com.coliving.platform.post.dto.CommentCreateResponseDTO;
import com.coliving.platform.post.dto.CommentRequestDTO;
import com.coliving.platform.post.dto.PostCreateResponseDTO;
import com.coliving.platform.post.dto.PostDetailDTO;
import com.coliving.platform.post.dto.PostListResponseDTO;
import com.coliving.platform.post.dto.PostRequestDTO;
import com.coliving.platform.post.dto.PostUpdateResponseDTO;
import com.coliving.platform.post.entity.PostCategory;
import com.coliving.platform.post.service.CommentService;
import com.coliving.platform.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 커뮤니티 게시판 — 게시글 · 댓글 작성 (API 5-1 ~ 5-6)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final CommentService commentService;

	/**
	 * 게시글 목록을 조회한다.
	 * 입주자는 내 건물, 관리자는 선택한 건물의 게시판을 볼 때 사용한다. 고정 공지를 별도 목록으로 반환한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param buildingId 건물 ID (예: 1), 생략 가능
	 * @param category 게시글 분류 (NOTICE / FREE / SHARE / MEETUP 중 하나), 생략 가능
	 * @param keyword 검색어 (예: 공용 공간)
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20, 1 이상, 100 이하
	 * @return 200 고정 공지와 게시글 페이지
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public PostListResponseDTO getPosts(
			@RequestHeader("X-User") Long userId,
			@RequestParam(required = false) Long buildingId,
			@RequestParam(required = false) PostCategory category,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		return postService.getPosts(userId, buildingId, category, keyword, page, size);
	}

	/**
	 * 게시글 상세를 조회한다.
	 * 입주자와 관리자가 글을 읽을 때 사용한다. 입주자는 같은 건물만 볼 수 있고 조회수는 24시간 단위로 중복을 제거한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param postId 게시글 ID (예: 1)
	 * @return 200 게시글 본문과 댓글·답글
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_RESIDENT 입주자 아님,
	 *         403 OTHER_BUILDING 다른 건물 접근,
	 *         404 POST_NOT_FOUND 게시글 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/{postId}")
	public PostDetailDTO getPost(@RequestHeader("X-User") Long userId, @PathVariable Long postId) {
		return postService.getPost(userId, postId);
	}

	/**
	 * 게시글을 작성한다.
	 * 입주자는 내 건물에 일반 글을, 관리자는 건물을 지정해 공지를 작성할 때 사용한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param request 분류·제목 (1~100자)·본문 (1~5000자), 관리자 공지는 건물 ID 필수
	 * @return 201 생성된 게시글 ID와 생성 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 400 INVALID_REQUEST 요청 값 오류,
	 *         403 NOT_OWNER 작성·변경 권한 없음,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         403 SANCTIONED 제재 중,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping
	public ResponseEntity<PostCreateResponseDTO> create(
			@RequestHeader("X-User") Long userId,
			@Valid @RequestBody PostRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(userId, request));
	}

	/**
	 * 내 게시글을 수정한다.
	 * 작성자가 본문과 분류를 고칠 때 사용한다. 일반 작성자는 공지로 변경할 수 없다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param postId 게시글 ID (예: 1)
	 * @param request 분류·제목 (1~100자)·본문 (1~5000자), 관리자 공지는 건물 ID 필수
	 * @return 200 수정된 게시글 ID와 수정 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_OWNER 작성·변경 권한 없음,
	 *         403 SANCTIONED 제재 중,
	 *         404 POST_NOT_FOUND 게시글 없음
	 */
	@PutMapping("/{postId}")
	public PostUpdateResponseDTO update(
			@RequestHeader("X-User") Long userId,
			@PathVariable Long postId,
			@Valid @RequestBody PostRequestDTO request) {
		return postService.update(userId, postId, request);
	}

	/**
	 * 내 게시글을 삭제한다.
	 * 작성자가 게시글을 지울 때 사용한다. 게시글은 소프트 삭제한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param postId 게시글 ID (예: 1)
	 * @return 204 삭제 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_OWNER 작성·변경 권한 없음,
	 *         404 POST_NOT_FOUND 게시글 없음
	 */
	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> delete(@RequestHeader("X-User") Long userId, @PathVariable Long postId) {
		postService.delete(userId, postId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 게시글에 댓글이나 답글을 작성한다.
	 * 입주자와 관리자가 글에 응답할 때 사용한다. 답글은 원댓글 아래 한 단계만 허용한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param postId 게시글 ID (예: 1)
	 * @param request 댓글 내용 (1~1000자)과 답글인 경우 원댓글 ID
	 * @return 201 생성된 댓글 ID와 생성 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 400 REPLY_DEPTH_EXCEEDED 답글 깊이 초과,
	 *         403 NOT_RESIDENT 입주자 아님,
	 *         403 OTHER_BUILDING 다른 건물 접근,
	 *         403 SANCTIONED 제재 중,
	 *         404 COMMENT_NOT_FOUND 댓글 없음,
	 *         404 POST_NOT_FOUND 게시글 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PostMapping("/{postId}/comments")
	public ResponseEntity<CommentCreateResponseDTO> createComment(
			@RequestHeader("X-User") Long userId,
			@PathVariable Long postId,
			@Valid @RequestBody CommentRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(userId, postId, request));
	}
}
