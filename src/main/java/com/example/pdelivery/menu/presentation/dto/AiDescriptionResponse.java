package com.example.pdelivery.menu.presentation.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiDescriptionResponse(
	String description,
	@JsonProperty("ai_request_id") UUID aiRequestId
) {
}
