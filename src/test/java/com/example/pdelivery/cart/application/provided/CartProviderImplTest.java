package com.example.pdelivery.cart.application.provided;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.cart.domain.CartEntity;
import com.example.pdelivery.cart.domain.CartLineEntity;
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
		Integer no1 = 1;
		UUID menuId1 = UUID.randomUUID();
		Integer quantity1 = 1;
		Integer no2 = 1;
		UUID menuId2 = UUID.randomUUID();
		Integer quantity2 = 2;

		List<CartLineEntity> cartLineEntities = List.of(
			new CartLineEntity(no1, menuId1, quantity1),
			new CartLineEntity(no2, menuId2, quantity2)
		);

		CartEntity cart = new CartEntity();

		ReflectionTestUtils.setField(cart, "userId", userId);
		ReflectionTestUtils.setField(cart, "storeId", storeId);
		ReflectionTestUtils.setField(cart, "cartLineEntities", cartLineEntities);

		UUID cartId = cartRepository.save(cart).getId();

		CartEntity savedCart = cartRepository.findById(cartId).orElseThrow();

		assertThat(savedCart.getId()).isEqualTo(cartId);
		assertThat(savedCart.getUserId()).isEqualTo(userId);
		assertThat(savedCart.getStoreId()).isEqualTo(storeId);
		assertThat(savedCart.getCartLineEntities()).hasSize(2);
		assertThat(savedCart.getCartLineEntities().get(0).menuId()).isEqualTo(menuId1);
		assertThat(savedCart.getCartLineEntities().get(0).quantity()).isEqualTo(quantity1);
		assertThat(savedCart.getCartLineEntities().get(1).menuId()).isEqualTo(menuId2);
		assertThat(savedCart.getCartLineEntities().get(1).quantity()).isEqualTo(quantity2);

	}
}