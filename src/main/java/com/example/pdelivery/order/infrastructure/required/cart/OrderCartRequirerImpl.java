package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.cart.application.provided.CartProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderCartRequirerImpl implements OrderCartRequirer {
	private final CartProvider cartOrderProvider;

	public List<CartData> getCartLines(UUID cartId) {
		return null;
	}
}
