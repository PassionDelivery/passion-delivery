package com.example.pdelivery.payment.infrastructure.required.order;

import java.util.UUID;

import com.example.pdelivery.shared.Requirer;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class PaymentOrderRequirerImpl implements PaymentOrderRequirer {

	// TODO
	@Override
	public PaymentOrderSummary getOrderSummary(UUID orderId) {
		throw new UnsupportedOperationException("구현 예정");
	}
}
