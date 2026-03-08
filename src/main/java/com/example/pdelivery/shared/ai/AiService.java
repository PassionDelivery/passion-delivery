package com.example.pdelivery.shared.ai;

import java.util.List;
import java.util.UUID;

public interface AiService {

	AiResponse generate(UUID userId, String systemPrompt, String userPrompt);

	List<AiRequestEntity> getHistory(UUID userId);
}
