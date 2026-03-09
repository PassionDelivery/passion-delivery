package com.example.pdelivery.payment.application.provider;

import java.util.UUID;

public interface PaymentProvider {
	void processPayment(UUID orderId, Long amount);
}
