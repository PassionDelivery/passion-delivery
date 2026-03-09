package com.example.pdelivery.store.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.store.domain.StoreEntity;

public record StoreResponse(
	UUID storeId,
	UUID ownerId,
	UUID categoryId,
	String name,
	String address,
	String phone,
	String status,
	LocalDateTime createdAt
) {

	public static StoreResponse from(StoreEntity entity) {
		return new StoreResponse(
			entity.getId(),
			entity.getOwnerId(),
			entity.getCategoryId(),
			entity.getStore().getName(),
			entity.getStore().getAddress(),
			entity.getStore().getPhone(),
			entity.getStatus().name(),
			entity.getCreatedAt()
		);
	}
}
