package com.example.pdelivery.menu.infrastructure.required.store;

import java.util.UUID;

public interface MenuStoreRequirer {

	StoreData getStore(UUID storeId);
}
