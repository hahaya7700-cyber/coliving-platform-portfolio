package com.coliving.platform.post.dto;

import com.coliving.platform.common.entity.ContentStatus;
import com.coliving.platform.common.entity.SenderType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 API의 원댓글·답글 응답.
 * 5-2 원댓글 (replies 포함)
 *
 * @param commentId 댓글 ID (예: 10)
 * @param authorId 작성 회원 ID (예: 5001)
 * @param authorType 작성자 유형 (USER / ADMIN 중 하나)
 * @param authorName 작성자 표시 이름 (예: 김민서)
 * @param authorRoomNumber 작성자 호실 (예: 302호)
 * @param content 내용 (예: 공용 공간 이용 문의)
 * @param status 현재 처리 상태 (ACTIVE / HIDDEN / DELETED 중 하나)
 * @param blocked 내가 차단한 작성자인지 여부 (예: false)
 * @param isMine 내가 작성한 항목인지 여부 (예: true)
 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
 * @param replies 원댓글에 달린 1단계 답글 목록
 * @author 오시문
 */
public record CommentDTO(
		Long commentId,
		Long authorId,
		SenderType authorType,
		String authorName,
		String authorRoomNumber,
		String content,
		ContentStatus status,
		boolean blocked,
		boolean isMine,
		LocalDateTime createdAt,
		List<ReplyDTO> replies
) {
}
