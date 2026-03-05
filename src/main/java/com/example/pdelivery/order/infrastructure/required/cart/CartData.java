package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.UUID;

public record CartData(
	UUID menuId,
	String menuName,
	Integer price,
	Integer quantity
) {
}
