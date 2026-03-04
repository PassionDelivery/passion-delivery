package com.example.pdelivery.order.infrastructure.required.address;

import java.util.UUID;

public interface OrderAddressRequirer {
	public String getAddress(UUID deliveryAddressId);
}
