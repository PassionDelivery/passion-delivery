package com.example.pdelivery.cart.application.provided;

import java.util.Optional;
import java.util.UUID;

public interface CartProvider {
	Optional<CartInfo> getCartInfo(UUID cartId);
}
