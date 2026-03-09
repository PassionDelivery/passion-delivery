package com.example.pdelivery.cart.application;

import java.util.UUID;

import com.example.pdelivery.cart.presentation.dto.CartAddItemRequest;
import com.example.pdelivery.cart.presentation.dto.CartResponse;
import com.example.pdelivery.cart.presentation.dto.CartUpdateItemRequest;

public interface CartService {

	CartResponse getMyCartItems(UUID userId);

	CartResponse addItem(UUID userId, CartAddItemRequest request);

	CartResponse updateItem(UUID userId, UUID itemId, CartUpdateItemRequest request);

	void removeItem(UUID userId, UUID itemId);

	void clearCart(UUID userId);
}
