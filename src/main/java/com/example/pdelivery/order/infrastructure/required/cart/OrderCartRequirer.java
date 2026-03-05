package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.List;
import java.util.UUID;

public interface OrderCartRequirer {
	public List<CartData> getCartLines(UUID cartId);
}
