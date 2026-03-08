package com.example.pdelivery.shared.ai;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;

@ExtendWith(MockitoExtension.class)
class AiServiceImplTest {

	@Mock
	private ChatClient.Builder chatClientBuilder;

	@Mock
	private ChatClient chatClient;

	@Mock
	private ChatClient.ChatClientRequestSpec requestSpec;

	@Mock
	private ChatClient.CallResponseSpec callResponseSpec;

	@Mock
	private AiRequestJpaRepository aiRequestJpaRepository;

	private AiServiceImpl aiService;

	@BeforeEach
	void setUp() {
		given(chatClientBuilder.build()).willReturn(chatClient);
		aiService = new AiServiceImpl(chatClientBuilder, aiRequestJpaRepository);
	}

	@Nested
	@DisplayName("AI 텍스트 생성")
	class Generate {

		@Test
		@DisplayName("대화 이력 없이 AI 텍스트를 생성하고 저장한다")
		void successWithoutHistory() {
			UUID userId = UUID.randomUUID();
			String systemPrompt = "시스템 프롬프트";
			String userPrompt = "사용자 프롬프트";

			given(aiRequestJpaRepository.findRecentByUserId(userId, 5)).willReturn(Collections.emptyList());
			given(chatClient.prompt()).willReturn(requestSpec);
			given(requestSpec.system(systemPrompt)).willReturn(requestSpec);
			given(requestSpec.user(userPrompt)).willReturn(requestSpec);
			given(requestSpec.call()).willReturn(callResponseSpec);
			given(callResponseSpec.content()).willReturn("AI 응답 텍스트");

			AiRequestEntity savedEntity = AiRequestEntity.create(userId, systemPrompt, userPrompt, "AI 응답 텍스트");
			given(aiRequestJpaRepository.save(any(AiRequestEntity.class))).willReturn(savedEntity);

			AiResponse response = aiService.generate(userId, systemPrompt, userPrompt);

			assertThat(response.content()).isEqualTo("AI 응답 텍스트");
			then(aiRequestJpaRepository).should().save(any(AiRequestEntity.class));
			then(requestSpec).should(never()).messages(anyList());
		}

		@Test
		@DisplayName("대화 이력이 있으면 메시지 히스토리에 포함한다")
		void successWithHistory() {
			UUID userId = UUID.randomUUID();
			String systemPrompt = "시스템 프롬프트";
			String userPrompt = "새 요청";

			AiRequestEntity historyEntity = AiRequestEntity.create(userId, "이전 시스템", "이전 요청", "이전 응답");
			given(aiRequestJpaRepository.findRecentByUserId(userId, 5)).willReturn(List.of(historyEntity));

			given(chatClient.prompt()).willReturn(requestSpec);
			given(requestSpec.system(systemPrompt)).willReturn(requestSpec);
			given(requestSpec.messages(anyList())).willReturn(requestSpec);
			given(requestSpec.user(userPrompt)).willReturn(requestSpec);
			given(requestSpec.call()).willReturn(callResponseSpec);
			given(callResponseSpec.content()).willReturn("새 AI 응답");

			AiRequestEntity savedEntity = AiRequestEntity.create(userId, systemPrompt, userPrompt, "새 AI 응답");
			given(aiRequestJpaRepository.save(any(AiRequestEntity.class))).willReturn(savedEntity);

			AiResponse response = aiService.generate(userId, systemPrompt, userPrompt);

			assertThat(response.content()).isEqualTo("새 AI 응답");

			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<Message>> messagesCaptor = ArgumentCaptor.forClass(List.class);
			then(requestSpec).should().messages(messagesCaptor.capture());

			List<Message> messages = messagesCaptor.getValue();
			assertThat(messages).hasSize(2);
			assertThat(messages.get(0).getText()).isEqualTo("이전 요청");
			assertThat(messages.get(1).getText()).isEqualTo("이전 응답");
		}

		@Test
		@DisplayName("요청과 응답을 p_ai_request에 저장한다")
		void savesRequestEntity() {
			UUID userId = UUID.randomUUID();
			String systemPrompt = "시스템";
			String userPrompt = "요청";

			given(aiRequestJpaRepository.findRecentByUserId(userId, 5)).willReturn(Collections.emptyList());
			given(chatClient.prompt()).willReturn(requestSpec);
			given(requestSpec.system(systemPrompt)).willReturn(requestSpec);
			given(requestSpec.user(userPrompt)).willReturn(requestSpec);
			given(requestSpec.call()).willReturn(callResponseSpec);
			given(callResponseSpec.content()).willReturn("응답");

			AiRequestEntity savedEntity = AiRequestEntity.create(userId, systemPrompt, userPrompt, "응답");
			given(aiRequestJpaRepository.save(any(AiRequestEntity.class))).willReturn(savedEntity);

			aiService.generate(userId, systemPrompt, userPrompt);

			ArgumentCaptor<AiRequestEntity> captor = ArgumentCaptor.forClass(AiRequestEntity.class);
			then(aiRequestJpaRepository).should().save(captor.capture());

			AiRequestEntity captured = captor.getValue();
			assertThat(captured.getUserId()).isEqualTo(userId);
			assertThat(captured.getSystemText()).isEqualTo(systemPrompt);
			assertThat(captured.getRequestText()).isEqualTo(userPrompt);
			assertThat(captured.getResponseText()).isEqualTo("응답");
		}
	}
}
