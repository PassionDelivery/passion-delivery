package com.example.pdelivery.store.application.provided;

import java.util.UUID;

public interface StoreProvider {

	StoreInfo getStore(UUID storeId);

	boolean existsById(UUID storeId);
}
