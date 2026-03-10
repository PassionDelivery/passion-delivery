package com.example.pdelivery.cart.infrastructure.required.menu;

import java.util.UUID;

public record MenuData(
	UUID menuId,
	UUID storeId,
	String name,
	Integer price
) {
}
