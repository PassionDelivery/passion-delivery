package com.example.pdelivery.shared.ai;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AiService {

	AiResponse generate(UUID userId, String systemPrompt, String userPrompt);

	Slice<AiRequestEntity> getHistory(UUID userId, Pageable pageable);

	boolean isOwnedByUser(UUID aiRequestId, UUID userId);
}
