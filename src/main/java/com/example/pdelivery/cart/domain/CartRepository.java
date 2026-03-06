package com.example.pdelivery.cart.domain;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {
	CartEntity save(CartEntity cart);

	Optional<CartEntity> findById(UUID cartId);
}
