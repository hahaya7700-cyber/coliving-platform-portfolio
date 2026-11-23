package com.coliving.platform.common.websocket;

import java.security.Principal;

/**
 * WebSocket 연결 사용자 — name = 회원 ID (convertAndSendToUser 의 user 값)
 *
 * @param name 이름 (예: 김민서)
 * @author 오시문
 */
public record StompPrincipal(String name) implements Principal {

	@Override
	public String getName() {
		return name;
	}
}
