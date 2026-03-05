package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.UUID;

public interface OrderCartRequirer {
	public CartData getCartLines(UUID cartId);
}
