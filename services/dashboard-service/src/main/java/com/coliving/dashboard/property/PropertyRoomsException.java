package com.coliving.dashboard.property;

/**
 * 매물 서비스 호실 목록 조회가 실패했을 때. 화면은 이미 저장해 둔 호실을 보여 준다
 * @author 조휘재
 * @version 1.0
 * @since 2026-11-12
 */
public class PropertyRoomsException extends RuntimeException {

    /* 실패 메시지와 원인을 담아 생성 */
    public PropertyRoomsException(String message, Throwable cause) {
        super(message, cause);
    }
}
