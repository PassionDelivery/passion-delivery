package com.example.pdelivery.cart.application.provided;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.cart.domain.CartRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartProviderImpl implements CartProvider {
	private final CartRepository cartRepository;

	@Transactional(readOnly = true)
	@Override
	public Optional<CartInfo> getCartInfo(UUID cartId) {
		return cartRepository.findById(cartId)
			.map(cart -> new CartInfo(
				cart.getUserId(),
				cart.getStoreId(),
				cart.getId(),
				cart.getCartLineEntities().stream()
					.map(line -> new CartLineInfo(line.no(), line.menuId(), line.quantity()))
					.toList()
			));
	}
}
