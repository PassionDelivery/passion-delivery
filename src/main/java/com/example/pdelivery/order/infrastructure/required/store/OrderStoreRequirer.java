package com.example.pdelivery.order.infrastructure.required.store;

import java.util.UUID;

public interface OrderStoreRequirer {
	String getStoreName(UUID storeId);

	UUID getOwnerId(UUID storeId);
}
