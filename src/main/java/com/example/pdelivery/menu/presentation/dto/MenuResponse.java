package com.example.pdelivery.menu.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.menu.domain.MenuEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MenuResponse(
	UUID menuId,
	UUID storeId,
	String name,
	Integer price,
	String description,
	Boolean isHidden,
	@JsonProperty("ai_request_id") UUID aiRequestId,
	LocalDateTime createdAt
) {

	public static MenuResponse from(MenuEntity entity) {

		return new MenuResponse(
			entity.getId(),
			entity.getStoreId(),
			entity.getMenu().getName(),
			entity.getMenu().getPrice(),
			entity.getMenu().getDescription(),
			entity.getMenu().getIsHidden(),
			entity.getAiRequestId(),
			entity.getCreatedAt()
		);
	}
}
