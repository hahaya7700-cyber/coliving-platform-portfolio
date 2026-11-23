package com.coliving.platform.note.dto;

import com.coliving.platform.common.entity.SenderType;
import com.coliving.platform.note.entity.NoteThreadStatus;
import com.coliving.platform.note.entity.NoteThreadType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 쪽지함 상세 조회 API 응답.
 * 3-2 쪽지함 상세
 *
 * @param threadId 쪽지함 ID (예: 1)
 * @param type 항목 유형 (CANDIDATE / RESIDENT / MEMBER / GUEST 중 하나)
 * @param status 현재 처리 상태 (UNANSWERED / ANSWERED 중 하나)
 * @param memo 쪽지함 내부 메모
 * @param user 회원 문의자 정보
 * @param guest 비회원 문의자 정보
 * @param notes 쪽지 내역 목록
 * @author 오시문
 */
public record AdminThreadDetailDTO(
		Long threadId,
		NoteThreadType type,
		NoteThreadStatus status,
		String memo,
		UserInfo user,
		GuestInfo guest,
		List<NoteInfo> notes
) {

	/**
	 * 관리자 쪽지함 상세 API의 회원 문의자 응답.
	 *
	 * @param userId 회원 ID (예: 5001)
	 * @param name 이름 (예: 김민서)
	 * @param buildingName 건물 이름 (예: 강남점)
	 * @param roomNumber 호실 (예: 302호)
	 * @author 오시문
	 */
	public record UserInfo(Long userId, String name, String buildingName, String roomNumber) {
	}

	/**
	 * 관리자 쪽지함 상세 API의 비회원 문의자 응답.
	 *
	 * @param name 이름 (예: 김민서)
	 * @param phone 휴대전화 번호 (예: 010-1234-5678)
	 * @param email 이메일 주소 (예: resident@example.com)
	 * @param privacyAgreedAt 개인정보 수집·이용 동의 일시
	 * @author 오시문
	 */
	public record GuestInfo(String name, String phone, String email, LocalDateTime privacyAgreedAt) {
	}

	/**
	 * 관리자 쪽지함 상세 API의 쪽지 내역 응답.
	 *
	 * @param noteId 쪽지 ID (예: 1)
	 * @param senderType 발신자 유형 (USER / ADMIN 중 하나)
	 * @param content 내용 (예: 공용 공간 이용 문의)
	 * @param createdAt 생성 일시 (예: 2026-11-12T10:00:00)
	 * @param readAt 쪽지를 읽은 일시 (안 읽었으면 null)
	 * @author 오시문
	 */
	public record NoteInfo(Long noteId, SenderType senderType, String content, LocalDateTime createdAt,
			LocalDateTime readAt) {
	}
}
