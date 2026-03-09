package com.example.pdelivery.shared.ai;

import java.util.UUID;

public record AiResponse(
	UUID aiRequestId,
	String content
) {
}
