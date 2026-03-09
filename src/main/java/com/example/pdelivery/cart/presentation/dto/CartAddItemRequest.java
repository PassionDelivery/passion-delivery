package com.example.pdelivery.cart.presentation.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartAddItemRequest(
	@NotNull
	@JsonProperty("store_id")
	UUID storeId,

	@NotNull
	@JsonProperty("menu_id")
	UUID menuId,

	@Min(1)
	@NotNull
	Integer quantity
) {
}
