package com.coliving.platform.faq.dto;

import java.util.List;

/**
 * FAQ 봇 질문 API의 답변과 근거 문서 응답.
 * 7-1 FAQ 봇 답변
 * answered: 문서 근거로 답했는지 / actions: 답을 못 했을 때 다음 행동 버튼 (INQUIRY 비회원 문의, NOTE 쪽지 문의)
 *
 * @param answered 근거 문서로 답변했는지 여부 (예: true)
 * @param answer FAQ 답변 (예: 매니저에게 문의해 주세요)
 * @param sources 답변의 근거 FAQ 문서 목록
 * @param actions 후속 행동 버튼 (INQUIRY: 비회원 문의, NOTE: 쪽지 문의)
 * @param cached 캐시에서 반환한 답변인지 여부 (예: false)
 * @author 오시문
 */
public record FaqAskResponseDTO(
		boolean answered,
		String answer,
		List<FaqSourceDTO> sources,
		List<String> actions,
		boolean cached
) {
}
