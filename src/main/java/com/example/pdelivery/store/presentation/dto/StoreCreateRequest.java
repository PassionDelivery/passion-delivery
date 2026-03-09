package com.example.pdelivery.store.presentation.dto;

import java.util.UUID;

public record StoreCreateRequest(
	UUID categoryId,
	String name,
	String address,
	String phone
) {
}
