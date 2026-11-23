package com.coliving.platform.post.controller;

import com.coliving.platform.post.dto.CommentUpdateRequestDTO;
import com.coliving.platform.post.dto.CommentUpdateResponseDTO;
import com.coliving.platform.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 커뮤니티 게시판 — 댓글 수정 · 삭제 (API 5-7, 5-8)
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 * 내 댓글을 수정한다.
	 * 작성자가 댓글 내용을 고칠 때 사용한다. 제재 중에는 수정할 수 없다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param commentId 댓글 ID (예: 10)
	 * @param request 수정할 댓글 내용 (공백만 입력 불가, 1~1000자)
	 * @return 200 수정된 댓글 ID와 수정 일시
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_OWNER 작성·변경 권한 없음,
	 *         403 SANCTIONED 제재 중,
	 *         404 COMMENT_NOT_FOUND 댓글 없음
	 */
	@PutMapping("/{commentId}")
	public CommentUpdateResponseDTO update(
			@RequestHeader("X-User") Long userId,
			@PathVariable Long commentId,
			@Valid @RequestBody CommentUpdateRequestDTO request) {
		return commentService.update(userId, commentId, request.content());
	}

	/**
	 * 내 댓글을 삭제한다.
	 * 작성자가 댓글을 지울 때 사용한다. 원본 데이터는 소프트 삭제한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param commentId 댓글 ID (예: 10)
	 * @return 204 삭제 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 403 NOT_OWNER 작성·변경 권한 없음,
	 *         404 COMMENT_NOT_FOUND 댓글 없음
	 */
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(@RequestHeader("X-User") Long userId, @PathVariable Long commentId) {
		commentService.delete(userId, commentId);
		return ResponseEntity.noContent().build();
	}
}
