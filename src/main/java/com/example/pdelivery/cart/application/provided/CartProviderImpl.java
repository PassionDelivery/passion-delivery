package com.example.pdelivery.cart.application.provided;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.cart.domain.CartEntity;
import com.example.pdelivery.cart.domain.CartLineEntity;
import com.example.pdelivery.cart.domain.CartRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CartProviderImpl implements CartProvider {
	private final CartRepository cartRepository;

	@Override
	public Optional<CartInfo> getCartInfo(UUID cartId) {
		CartEntity cart = cartRepository.findById(cartId);

		List<CartLineInfo> cartLineInfos = new ArrayList<>();

		for (int i = 0; i < cart.getCartLineEntities().size(); ++i) {
			CartLineEntity cartLineEntity = cart.getCartLineEntities().get(i);
			cartLineInfos.add(new CartLineInfo(i, cartLineEntity.menuId(), cartLineEntity.quantity()));
		}

		CartInfo cartInfo = new CartInfo(cart.getUserId(), cart.getStoreId(), cart.getId(), cartLineInfos);

		return Optional.of(cartInfo);
	}
}
