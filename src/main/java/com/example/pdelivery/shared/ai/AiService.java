package com.example.pdelivery.shared.ai;

import java.util.UUID;

public interface AiService {

	AiResponse generate(UUID userId, String systemPrompt, String userPrompt);
}
