package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.error.OrderErrorCode.*;

import java.util.UUID;

import com.example.pdelivery.order.error.OrderException;

public class OrderRequest {
	public record OrderCreateRequest(UUID cartId, UUID deliveryAddressId) {

		public OrderCreateRequest {
			validate(cartId, "cartId");
			validate(deliveryAddressId, "deliveryAddressId");
		}

		private void validate(UUID id, String fieldName) {
			if (id == null) {
				throw new OrderException(REQUIRED_PARAMETER_MISSING, fieldName + " is missing");
			}
		}
	}
}
