package com.example.pdelivery.menu.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MenuCreateRequest(
	String name,
	Integer price,
	String description,
	@JsonProperty("use_ai_description") Boolean useAiDescription,
	@JsonProperty("ai_request_text") String aiRequestText
) {
}