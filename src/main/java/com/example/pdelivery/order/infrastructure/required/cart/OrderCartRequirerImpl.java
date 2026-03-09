package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.cart.application.provided.CartInfo;
import com.example.pdelivery.cart.application.provided.CartProvider;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderCartRequirerImpl implements OrderCartRequirer {

	private final CartProvider cartProvider;

	@Override
	public CartData getCartLines(UUID cartId) {
		CartInfo cartInfo = cartProvider.getCartInfo(cartId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.REQUIRED_PARAMETER_MISSING, "cart not found"));

		if (cartInfo.storeId() == null) {
			throw new OrderException(OrderErrorCode.REQUIRED_PARAMETER_MISSING, "storeId is missing");
		}
		if (cartInfo.cartItems() == null || cartInfo.cartItems().isEmpty()) {
			throw new OrderException(OrderErrorCode.CART_EMPTY);
		}

		List<CartData.CartItems> cartItems = cartInfo.cartItems().stream()
			.map(item -> new CartData.CartItems(item.menuId(), item.quantity()))
			.toList();

		return new CartData(cartInfo.storeId(), cartItems);
	}
}
