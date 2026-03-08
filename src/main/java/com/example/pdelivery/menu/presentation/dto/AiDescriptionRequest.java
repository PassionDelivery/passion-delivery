package com.example.pdelivery.menu.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;

public record AiDescriptionRequest(
	@Size(max = 500)
	@JsonProperty("request_text") String requestText
) {
}
