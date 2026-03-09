package com.example.pdelivery.order.infrastructure.required.store;

import java.util.UUID;

public interface OrderStoreRequirer {
	public String getStoreName(UUID storeId);
}
