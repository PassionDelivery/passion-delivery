package com.example.pdelivery.payment.infrastructure.required.order;

import java.util.UUID;

public record PaymentOrderSummary(
	UUID orderId, UUID customerId, UUID storeId, long totalAmount
) {
}
