package com.example.pdelivery.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.infrastructure.required.address.OrderAddressRequirer;
import com.example.pdelivery.order.infrastructure.required.cart.CartData;
import com.example.pdelivery.order.infrastructure.required.cart.OrderCartRequirer;
import com.example.pdelivery.order.infrastructure.required.menu.MenuData;
import com.example.pdelivery.order.infrastructure.required.menu.OrderMenuRequirer;
import com.example.pdelivery.order.infrastructure.required.payment.OrderPaymentRequirer;

@ExtendWith(MockitoExtension.class) // Spring을 다 띄우지 않고 Mockito만 사용해서 빠릅니다.
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderCartRequirer orderCartRequirer;
	@Mock
	private OrderAddressRequirer orderAddressRequirer;
	@Mock
	private OrderPaymentRequirer orderPaymentRequirer;
	@Mock
	private OrderMenuRequirer orderMenuRequirer;

	@InjectMocks
	private OrderServiceImpl orderService; // Mock들을 이 서비스에 주입

	@Test
	@DisplayName("주문 생성 성공 테스트")
	void createOrder_Success() {
		// 가짜 데이터
		UUID storeId = UUID.randomUUID();
		UUID cartId = UUID.randomUUID();
		UUID addressId = UUID.randomUUID();

		UUID chickenId = UUID.randomUUID();
		UUID pizzaId = UUID.randomUUID();
		List<UUID> menuIds = List.of(chickenId, pizzaId);

		CartData mockCartData = new CartData(storeId, menuIds);

		List<MenuData> menuData = new ArrayList<>();
		menuData.add(new MenuData(chickenId, "chiken", 20000, 1));
		menuData.add(new MenuData(pizzaId, "pizza", 17000, 2));

		OrderRequest.OrderCreateRequest req = new OrderRequest.OrderCreateRequest(cartId, addressId);

		// Stubbing
		when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
		when(orderCartRequirer.getCartLines(cartId)).thenReturn(mockCartData);
		when(orderPaymentRequirer.processPayment(any(), eq(54000))).thenReturn(true);
		when(orderMenuRequirer.getMenus(menuIds)).thenReturn(menuData);

		// 실행
		Order result = orderService.createOrder(req);

		// Assert
		Order.OrderView orderView = new Order.OrderView(result);

		assertThat(result).isNotNull();
		assertThat(orderView.getAddress()).isEqualTo("서울시 강남구");
		assertThat(orderView.getTotalPrice()).isEqualTo(54000);

		// 호출 여부 확인 -> "실제로 리포지토리에 저장이 요청되었는가?"
		verify(orderRepository, times(1)).save(any(Order.class));
	}
}
