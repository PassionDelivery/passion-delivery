package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;

public interface OrderPaymentRequirer {
	public boolean processPayment(UUID customerId, CreatePaymentRequest request);

	public boolean cancelPaymentByOrder(UUID orderId);
}
