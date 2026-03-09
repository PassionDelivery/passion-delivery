package com.example.pdelivery.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.pdelivery.cart.domain.CartEntity;
import com.example.pdelivery.cart.domain.CartRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartJpaPersistence implements CartRepository {
	private final CartJpaRepository cartJpaRepository;

	@Override
	public CartEntity save(CartEntity cart) {
		return cartJpaRepository.save(cart);
	}

	@Override
	public Optional<CartEntity> findById(UUID cartId) {
		return cartJpaRepository.findById(cartId);
	}

	@Override
	public Optional<CartEntity> findByUserId(UUID userId) {
		return cartJpaRepository.findByUserId(userId);
	}
}
