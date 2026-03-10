package com.example.pdelivery.cart.infrastructure.required.menu;

import java.util.UUID;

public interface CartMenuRequirer {
	MenuData getMenu(UUID menuId);

	void validateMenuBelongsToStore(UUID menuId, UUID storeId);
}
