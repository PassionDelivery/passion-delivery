package com.example.pdelivery.payment.application.provider;

import java.util.UUID;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;

public interface PaymentProvider {
	boolean processPayment(UUID customerId, CreatePaymentRequest request);

	boolean cancelPaymentByOrder(UUID orderId);
}
