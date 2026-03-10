package com.example.pdelivery.payment.infrastructure.required.order;

import java.util.UUID;

import com.example.pdelivery.shared.annotations.Requirer;

@Requirer
public class PaymentStoreRequirerImpl implements PaymentStoreRequirer {

	// TODO: PROVIDER 필요
	@Override
	public UUID getOwnerStoreId() {
		return null;
	}
}
