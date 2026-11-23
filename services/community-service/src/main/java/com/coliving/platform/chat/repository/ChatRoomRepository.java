package com.coliving.platform.chat.repository;

import com.coliving.platform.chat.entity.ChatRoomEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 채팅방의 저장·조회 접근을 제공한다.
 *
 * @author 오시문
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

	Optional<ChatRoomEntity> findByPairKey(String pairKey);
}
