package com.example.pdelivery.payment.application.provider;

import java.util.UUID;

public interface PaymentProvider {
	boolean processPayment(UUID orderId, Long amount);

	boolean cancelPaymentByOrder(UUID orderId);
}
