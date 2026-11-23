package com.coliving.platform.safety.service;

import com.coliving.platform.common.exception.CommunityException;
import com.coliving.platform.common.exception.ErrorCode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 제재 확인 — 이용 제한·정지 중이면 글·댓글·채팅·신고 작성 막기 (403 SANCTIONED, 명세서 0-6)
 * 읽기·쪽지·FAQ 봇·차단 관리는 계속 가능
 *
 * @author 오시문
 */
@Component
@RequiredArgsConstructor
public class SanctionGuard {

	private static final DateTimeFormatter END_FORMAT = DateTimeFormatter.ofPattern("M월 d일 HH:mm");

	private final SanctionQueryService sanctionQueryService;

	/**
	 * 현재 제재를 확인해 커뮤니티 쓰기 가능 여부를 검증한다.
	 *
	 * @throws com.coliving.platform.common.exception.CommunityException 403 SANCTIONED 제재 중
	 */
	public void checkCanWrite(Long userId) {
		sanctionQueryService.findActive(userId).ifPresent(sanction -> {
			Map<String, Object> properties = new HashMap<>();
			properties.put("sanctionType", sanction.getSanctionType().name());
			properties.put("endsAt", sanction.getEndsAt() != null ? sanction.getEndsAt().toString() : null);
			String detail = sanction.getEndsAt() != null
					? "커뮤니티 이용이 제한된 상태입니다. (" + sanction.getEndsAt().format(END_FORMAT) + " 해제)"
					: "관리자가 해제할 때까지 커뮤니티 이용이 정지된 상태입니다.";
			throw new CommunityException(ErrorCode.SANCTIONED, detail, properties);
		});
	}
}
