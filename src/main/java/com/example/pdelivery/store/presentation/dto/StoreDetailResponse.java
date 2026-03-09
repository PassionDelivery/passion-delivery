package com.example.pdelivery.store.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.store.domain.StoreEntity;

public record StoreDetailResponse(
	UUID storeId,
	UUID ownerId,
	UUID categoryId,
	String name,
	String address,
	String phone,
	String status,
	String rejectCode,
	String rejectReason,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static StoreDetailResponse from(StoreEntity entity) {
		return new StoreDetailResponse(
			entity.getId(),
			entity.getOwnerId(),
			entity.getCategoryId(),
			entity.getStore().getName(),
			entity.getStore().getAddress(),
			entity.getStore().getPhone(),
			entity.getStatus().name(),
			entity.getRejectCode(),
			entity.getRejectReason(),
			entity.getCreatedAt(),
			entity.getUpdatedAt()
		);
	}
}
