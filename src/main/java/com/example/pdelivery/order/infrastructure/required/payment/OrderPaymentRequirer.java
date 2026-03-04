package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

public interface OrderPaymentRequirer {
	public Boolean processPayment(UUID orderId, Integer amount);
}
