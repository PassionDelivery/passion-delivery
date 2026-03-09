package com.example.pdelivery.cart.presentation.dto;

import java.util.List;
import java.util.UUID;

import com.example.pdelivery.cart.domain.CartEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CartResponse(
	@JsonProperty("cart_id")
	UUID cartId,
	@JsonProperty("store_id")
	UUID storeId,
	List<CartLineResponse> items
) {
	public static CartResponse from(CartEntity cart) {
		return new CartResponse(
			cart.getId(),
			cart.getStoreId(),
			cart.getCartLineEntities().stream()
				.map(CartLineResponse::from)
				.toList()
		);
	}
}
