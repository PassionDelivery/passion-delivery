package com.example.pdelivery.menu.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.shared.ai.AiRequestEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AiDescriptionHistoryResponse(
	@JsonProperty("ai_request_id") UUID aiRequestId,
	@JsonProperty("request_text") String requestText,
	@JsonProperty("response_text") String responseText,
	@JsonProperty("created_at") LocalDateTime createdAt
) {

	public static AiDescriptionHistoryResponse from(AiRequestEntity entity) {
		return new AiDescriptionHistoryResponse(
			entity.getId(),
			entity.getRequestText(),
			entity.getResponseText(),
			entity.getCreatedAt()
		);
	}
}
