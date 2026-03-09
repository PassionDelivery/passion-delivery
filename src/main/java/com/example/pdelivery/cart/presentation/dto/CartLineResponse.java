package com.example.pdelivery.cart.presentation.dto;

import java.util.UUID;

import com.example.pdelivery.cart.domain.CartLineEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CartLineResponse(
	Integer no,
	@JsonProperty("menu_id")
	UUID menuId,
	Integer quantity
) {
	public static CartLineResponse from(CartLineEntity line) {
		return new CartLineResponse(line.no(), line.menuId(), line.quantity());
	}
}
