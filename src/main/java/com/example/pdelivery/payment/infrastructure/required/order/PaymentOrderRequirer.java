package com.example.pdelivery.payment.infrastructure.required.order;

import java.util.UUID;

public interface PaymentOrderRequirer {
	PaymentOrderSummary getOrderSummary(UUID orderId);
}

