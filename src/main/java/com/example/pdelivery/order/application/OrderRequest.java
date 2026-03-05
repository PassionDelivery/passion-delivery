package com.example.pdelivery.order.application;

import java.util.UUID;

public class OrderRequest {
	public record OrderCreateRequest(UUID cartId,
									 UUID deliveryAddressId,
									 UUID storeId) {

	}
}
