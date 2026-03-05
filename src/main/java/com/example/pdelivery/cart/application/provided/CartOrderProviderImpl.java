package com.example.pdelivery.cart.application.provided;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.infrastructure.required.cart.CartData;

@Component
public class CartOrderProviderImpl implements CartOrderProvider {

	@Override
	public List<CartData> getCartLines(UUID cartId) {
		return List.of();
	}
}
