package com.example.pdelivery.store.presentation.dto;

import java.util.UUID;

import com.example.pdelivery.store.domain.StoreEntity;

public record StoreSearchResponse(
	UUID storeId,
	UUID categoryId,
	String name,
	String address
) {

	public static StoreSearchResponse from(StoreEntity entity) {
		return new StoreSearchResponse(
			entity.getId(),
			entity.getCategoryId(),
			entity.getStore().getName(),
			entity.getStore().getAddress()
		);
	}
}
