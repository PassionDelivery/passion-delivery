package com.example.pdelivery.cart.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartUpdateItemRequest(
	@Min(1)
	@NotNull
	Integer quantity
) {
}
