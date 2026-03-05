package com.example.pdelivery.cart.domain;

import java.util.UUID;

public interface CartRepository {
	CartEntity save(CartEntity cart);

	CartEntity findById(UUID cartId);
}
