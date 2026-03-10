package com.example.pdelivery.shared.ai;

import java.util.UUID;

import com.example.pdelivery.shared.jpa.BaseEntityOnlyCreated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_ai_request")
public class AiRequestEntity extends BaseEntityOnlyCreated {

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "system_text", columnDefinition = "TEXT")
	private String systemText;

	@Column(name = "request_text", columnDefinition = "TEXT")
	private String requestText;

	@Column(name = "response_text", columnDefinition = "TEXT")
	private String responseText;

	@Builder
	private AiRequestEntity(UUID userId, String systemText, String requestText, String responseText) {
		this.userId = userId;
		this.systemText = systemText;
		this.requestText = requestText;
		this.responseText = responseText;
	}

	public static AiRequestEntity create(UUID userId, String systemText, String requestText, String responseText) {
		return AiRequestEntity.builder()
			.userId(userId)
			.systemText(systemText)
			.requestText(requestText)
			.responseText(responseText)
			.build();
	}
}
