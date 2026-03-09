package com.example.pdelivery.payment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.payment.domain.PaymentStatus;

public record ApprovePaymentResponse(
	UUID paymentId,
	String providerPaymentKey,
	PaymentStatus paymentStatus,
	LocalDateTime approvedAt
) {
}
