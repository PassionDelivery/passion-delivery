package com.example.pdelivery.order.infrastructure.required.cart;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderCartRequirerImpl implements OrderCartRequirer {
	// private final CartProvider cartProvider;
	private final CartData cartData;

	public CartData getCartLines(UUID cartId) {

		//데이터가 없거나 빈 경우
		if (cartData.storeId() == null) {
			throw new OrderException(OrderErrorCode.REQUIRED_PARAMETER_MISSING, "storeId is missing");
		}
		if (cartData.menuIds().isEmpty()) {
			throw new OrderException(OrderErrorCode.CART_EMPTY);
		}
		return cartData;
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
