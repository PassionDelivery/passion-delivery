package com.example.pdelivery.cart.infrastructure.required.menu;

import java.util.UUID;

public interface CartMenuRequirer {
	void validateMenuExists(UUID menuId);
}
