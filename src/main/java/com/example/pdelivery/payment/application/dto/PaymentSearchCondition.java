package com.example.pdelivery.payment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentStatus;

public record PaymentSearchCondition(
	UUID storeId,
	UUID customerId,
	PaymentStatus paymentStatus,
	PaymentProvider paymentProvider,
	PaymentMethod paymentMethod,
	LocalDateTime from,
	LocalDateTime to,
	String keyword
) {
}
