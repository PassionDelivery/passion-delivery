package com.example.pdelivery.cart.application.provided;

import java.util.List;
import java.util.UUID;

public record CartInfo(
	UUID userId,
	UUID storeId,
	UUID cartId,
	List<CartLineInfo> cartItems
) {
}
