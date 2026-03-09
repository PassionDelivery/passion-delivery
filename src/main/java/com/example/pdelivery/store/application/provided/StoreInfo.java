package com.example.pdelivery.store.application.provided;

import java.util.UUID;

public record StoreInfo(
	UUID storeId,
	UUID ownerId,
	String storeName
) {
}
