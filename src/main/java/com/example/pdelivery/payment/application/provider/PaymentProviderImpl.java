package com.example.pdelivery.payment.application.provider;

import java.util.UUID;

import com.example.pdelivery.payment.application.PaymentService;
import com.example.pdelivery.shared.Provider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
public class PaymentProviderImpl implements PaymentProvider {

	private final PaymentService paymentService;

	@Override
	@Transactional
	public boolean processPayment(UUID orderId, Long amount) {
		return paymentService.approvePaymentByOrder(orderId, amount);
	}

	@Override
	@Transactional
	public boolean cancelPaymentByOrder(UUID orderId) {
		return paymentService.cancelPaymentByOrder(orderId);
	}
}

