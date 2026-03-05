package com.example.pdelivery.cart.application.provided;

import java.util.List;
import java.util.UUID;

import com.example.pdelivery.order.infrastructure.required.cart.CartData;

public interface CartOrderProvider {
	public List<CartData> getCartLines(UUID cartId);
}
