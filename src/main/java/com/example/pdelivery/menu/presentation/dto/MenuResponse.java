package com.example.pdelivery.menu.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.menu.domain.MenuEntity;

public record MenuResponse(
	UUID menuId,
	UUID storeId,
	String name,
	Integer price,
	String description,
	Boolean isHidden,
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
			entity.getCreatedAt()
		);
	}
}