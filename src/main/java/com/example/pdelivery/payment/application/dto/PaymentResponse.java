package com.example.pdelivery.payment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentStatus;

public record PaymentResponse(
	UUID paymentId,
	UUID orderId,
	UUID storeId,
	PaymentProvider paymentProvider,
	PaymentMethod paymentMethod,
	long amount,
	PaymentStatus paymentStatus,
	String providerPaymentKey,
	LocalDateTime approvedAt,
	LocalDateTime createdAt,
	LocalDateTime updatedAt

) {
	public static PaymentResponse from(Payment payment) {
		return new PaymentResponse(
			payment.getId(),
			payment.getOrderId(),
			payment.getStoreId(),
			payment.getPaymentProvider(),
			payment.getPaymentMethod(),
			payment.getAmount(),
			payment.getPaymentStatus(),
			payment.getProviderPaymentKey(),
			payment.getApprovedAt(),
			payment.getCreatedAt(),
			payment.getUpdatedAt()
		);
	}
}
