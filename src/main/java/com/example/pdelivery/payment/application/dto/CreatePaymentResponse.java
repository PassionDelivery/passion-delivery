package com.example.pdelivery.payment.application.dto;

import java.util.UUID;

import com.example.pdelivery.payment.domain.Payment;

public record CreatePaymentResponse(UUID paymentId) {

	public static CreatePaymentResponse from(Payment payment) {
		return new CreatePaymentResponse(payment.getId());
	}
}
