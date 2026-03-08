package com.example.pdelivery.shared.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AiServiceImpl implements AiService {

	private static final int MAX_HISTORY_SIZE = 5;

	private final ChatClient chatClient;
	private final AiRequestJpaRepository aiRequestJpaRepository;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, AiRequestJpaRepository aiRequestJpaRepository) {
		this.chatClient = chatClientBuilder.build();
		this.aiRequestJpaRepository = aiRequestJpaRepository;
	}

	@Override
	public AiResponse generate(UUID userId, String systemPrompt, String userPrompt) {
		List<AiRequestEntity> history = aiRequestJpaRepository.findRecentByUserId(userId, MAX_HISTORY_SIZE);

		ChatClientRequestSpec requestSpec = chatClient.prompt()
			.system(systemPrompt);

		// 과거 대화 이력을 오래된 순서로 메시지에 추가 (history는 최신순이므로 역순 처리)
		List<AiRequestEntity> chronological = new ArrayList<>(history);
		Collections.reverse(chronological);
		List<Message> messages = new ArrayList<>();
		for (AiRequestEntity entity : chronological) {
			messages.add(new UserMessage(entity.getRequestText()));
			messages.add(new AssistantMessage(entity.getResponseText()));
		}

		if (!messages.isEmpty()) {
			requestSpec.messages(messages);
		}

		String content = requestSpec
			.user(userPrompt)
			.call()
			.content();

		AiRequestEntity entity = AiRequestEntity.create(userId, systemPrompt, userPrompt, content);
		AiRequestEntity saved = aiRequestJpaRepository.save(entity);

		log.info("AI 요청 저장 완료: aiRequestId={}, userId={}, historySize={}", saved.getId(), userId, history.size());

		return new AiResponse(saved.getId(), content);
	}

	@Override
	@Transactional(readOnly = true)
	public List<AiRequestEntity> getHistory(UUID userId) {
		return aiRequestJpaRepository.findRecentByUserId(userId, MAX_HISTORY_SIZE);
	}
}
