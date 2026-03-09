package com.example.pdelivery.cart.application.provided;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.cart.domain.CartEntity;
import com.example.pdelivery.cart.domain.CartRepository;

@Transactional
@SpringBootTest
class CartProviderImplTest {
	@Autowired
	CartRepository cartRepository;

	@Test
	void getCartInfo() {
		UUID userId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		UUID menuId1 = UUID.randomUUID();
		int quantity1 = 1;
		UUID menuId2 = UUID.randomUUID();
		int quantity2 = 2;

		CartEntity cart = CartEntity.create(userId, storeId);
		cart.addOrUpdateItem(menuId1, quantity1);
		cart.addOrUpdateItem(menuId2, quantity2);

		UUID cartId = cartRepository.save(cart).getId();

		CartEntity savedCart = cartRepository.findById(cartId).orElseThrow();

		assertThat(savedCart.getId()).isEqualTo(cartId);
		assertThat(savedCart.getUserId()).isEqualTo(userId);
		assertThat(savedCart.getStoreId()).isEqualTo(storeId);
		assertThat(savedCart.getCartLineEntities()).hasSize(2);
	}
}