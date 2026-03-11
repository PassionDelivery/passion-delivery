package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

public interface OrderPaymentRequirer {
	public boolean processPayment(UUID orderId, Long amount);

	public boolean cancelPaymentByOrder(UUID orderId);
}
