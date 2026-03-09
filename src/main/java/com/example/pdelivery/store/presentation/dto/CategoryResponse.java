package com.example.pdelivery.store.presentation.dto;

import java.util.UUID;

import com.example.pdelivery.store.domain.CategoryEntity;

public record CategoryResponse(UUID id, String name) {

	public static CategoryResponse from(CategoryEntity entity) {
		return new CategoryResponse(entity.getId(), entity.getName());
	}
}
