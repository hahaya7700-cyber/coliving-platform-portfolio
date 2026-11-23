package com.coliving.platform.notification.controller;

import com.coliving.platform.common.dto.PageResponse;
import com.coliving.platform.notification.dto.NotificationItemDTO;
import com.coliving.platform.notification.dto.UnreadSummaryDTO;
import com.coliving.platform.notification.service.NotificationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 알림 (API 6-1 ~ 6-4) — 회원 · 관리자 공용
 *
 * @author 오시문
 */
@RestController
@RequestMapping("/community-service/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	/**
	 * 내 알림 목록을 조회한다.
	 * 회원과 관리자가 알림 화면을 열 때 사용한다. 관리자는 공용 알림과 본인 알림을 함께 조회한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param unreadOnly 안 읽은 알림만 조회할지 여부 (기본 false), 기본 false
	 * @param page 0부터 시작하는 페이지 번호 (예: 0), 기본 0, 0 이상
	 * @param size 한 페이지 항목 수 (예: 20), 기본 20, 1 이상, 100 이하
	 * @return 200 최신순 알림 페이지
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping
	public PageResponse<NotificationItemDTO> getNotifications(
			@RequestHeader("X-User") Long userId,
			@RequestParam(defaultValue = "false") boolean unreadOnly,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
		return notificationService.getNotifications(userId, unreadOnly, page, size);
	}

	/**
	 * 안 읽은 쪽지·채팅·알림 수를 조회한다.
	 * 회원과 관리자의 위젯이 배지를 표시할 때 사용한다. total은 쪽지와 채팅 수의 합계다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 200 각 기능의 안 읽은 수와 위젯 합계
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@GetMapping("/unread-summary")
	public UnreadSummaryDTO getUnreadSummary(@RequestHeader("X-User") Long userId) {
		return notificationService.getUnreadSummary(userId);
	}

	/**
	 * 알림 하나를 읽음 처리한다.
	 * 수신자가 알림을 확인했을 때 사용한다. 본인이 볼 수 있는 알림만 처리한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @param notificationId 알림 ID (예: 1)
	 * @return 204 읽음 처리 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 404 NOTIFICATION_NOT_FOUND 조회 가능한 알림 없음,
	 *         503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<Void> markRead(@RequestHeader("X-User") Long userId, @PathVariable Long notificationId) {
		notificationService.markRead(userId, notificationId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 내 알림을 모두 읽음 처리한다.
	 * 회원과 관리자가 알림 전체 읽기를 선택할 때 사용한다. 관리자는 공용 알림도 함께 처리한다.
	 *
	 * @param userId Gateway 가 로그인 쿠키로 넣어주는 회원 ID
	 * @return 204 전체 읽음 처리 완료, 응답 본문 없음
	 * @throws com.coliving.platform.common.exception.CommunityException 503 SERVICE_UNAVAILABLE gRPC 조회 실패
	 */
	@PatchMapping("/read-all")
	public ResponseEntity<Void> markAllRead(@RequestHeader("X-User") Long userId) {
		notificationService.markAllRead(userId);
		return ResponseEntity.noContent().build();
	}
}
