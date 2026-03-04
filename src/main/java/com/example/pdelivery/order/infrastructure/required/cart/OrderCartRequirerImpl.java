package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.cart.application.provided.CartOrderProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderCartRequirerImpl implements OrderCartRequirer {
	private final CartOrderProvider cartOrderProvider;

	public List<CartData> getCartLines(UUID cartId) {
		List<CartData> cartData = cartOrderProvider.getCartLines(cartId);
		return cartData;
	}
}
