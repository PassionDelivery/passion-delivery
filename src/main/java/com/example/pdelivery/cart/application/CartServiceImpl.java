package com.example.pdelivery.cart.application;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.cart.domain.CartEntity;
import com.example.pdelivery.cart.domain.CartRepository;
import com.example.pdelivery.cart.error.CartErrorCode;
import com.example.pdelivery.cart.error.CartException;
import com.example.pdelivery.cart.infrastructure.required.menu.CartMenuRequirer;
import com.example.pdelivery.cart.presentation.dto.CartAddItemRequest;
import com.example.pdelivery.cart.presentation.dto.CartResponse;
import com.example.pdelivery.cart.presentation.dto.CartUpdateItemRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartMenuRequirer cartMenuRequirer;

	@Override
	@Transactional(readOnly = true)
	public CartResponse getMyCartItems(UUID userId) {
		CartEntity cart = cartRepository.findByUserId(userId)
			.orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND));
		return CartResponse.from(cart);
	}

	@Override
	public CartResponse addItem(UUID userId, CartAddItemRequest request) {
		cartMenuRequirer.validateMenuBelongsToStore(request.menuId(), request.storeId());

		CartEntity cart = cartRepository.findByUserId(userId)
			.orElseGet(() -> CartEntity.create(userId, request.storeId()));

		if (!cart.isSameStore(request.storeId())) {
			cart.resetForStore(request.storeId());
		}

		cart.addOrUpdateItem(request.menuId(), request.quantity());

		try {
			return CartResponse.from(cartRepository.save(cart));
		} catch (DataIntegrityViolationException e) {
			CartEntity existing = findCartByUserOrThrow(userId);
			if (!existing.isSameStore(request.storeId())) {
				existing.resetForStore(request.storeId());
			}
			existing.addOrUpdateItem(request.menuId(), request.quantity());
			return CartResponse.from(cartRepository.save(existing));
		}
	}

	@Override
	public CartResponse updateItem(UUID userId, UUID itemId, CartUpdateItemRequest request) {
		CartEntity cart = findCartByUserOrThrow(userId);
		cart.updateItemQuantity(itemId, request.quantity());
		return CartResponse.from(cartRepository.save(cart));
	}

	@Override
	public void removeItem(UUID userId, UUID itemId) {
		CartEntity cart = findCartByUserOrThrow(userId);
		cart.removeItem(itemId);
		cartRepository.save(cart);
	}

	@Override
	public void clearCart(UUID userId) {
		CartEntity cart = findCartByUserOrThrow(userId);
		cart.resetForStore(null);
		cartRepository.save(cart);
	}

	private CartEntity findCartByUserOrThrow(UUID userId) {
		return cartRepository.findByUserId(userId)
			.orElseThrow(() -> new CartException(CartErrorCode.CART_NOT_FOUND));
	}
}
