package com.example.pdelivery.order.application;

import static com.example.pdelivery.order.infrastructure.required.cart.CartData.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;
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

	// 공통 가짜 데이터 필드
	private UUID storeId;
	private UUID cartId;
	private UUID customerId;
	// private UUID addressId;
	private String address;
	private UUID chickenId;
	private UUID pizzaId;
	private List<CartItems> items;
	private List<UUID> menuIds;
	private OrderRequest.OrderCreateRequest req;
	private CartData mockCartData;
	private List<MenuData> menuData = new ArrayList<>();

	@BeforeEach
	void setUp() {
		customerId = UUID.randomUUID();
		storeId = UUID.randomUUID();
		cartId = UUID.randomUUID();
		address = "서울시 중구 14-25";

		req = new OrderRequest.OrderCreateRequest(cartId, address);

		chickenId = UUID.randomUUID();
		pizzaId = UUID.randomUUID();
		items = new ArrayList<>(List.of(new CartItems(chickenId, 1), new CartItems(pizzaId, 2)));
		menuIds = new ArrayList<>(List.of(chickenId, pizzaId));

		mockCartData = new CartData(storeId, items);

		menuData.add(new MenuData(chickenId, "chiken", 20000));
		menuData.add(new MenuData(pizzaId, "pizza", 17000));

	}

	@Nested
	@DisplayName("주문 생성 테스트")
	class CreateOrder {

		@Test
		@DisplayName("주문 생성 성공 테스트")
		void createOrder_Success() {
			// Stubbing
			// when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
			when(orderCartRequirer.getCartLines(cartId)).thenReturn(mockCartData);
			when(orderPaymentRequirer.processPayment(any(), any())).thenReturn(true);
			when(orderMenuRequirer.getMenus(menuIds)).thenReturn(menuData);

			Order result = orderService.createOrder(customerId, req);

			// Assert
			Order.OrderView orderView = new Order.OrderView(result);

			assertThat(result).isNotNull();
			assertThat(orderView.getAddress()).isEqualTo("서울시 중구 14-25");
			assertThat(orderView.getTotalPrice()).isEqualTo(54000);

			assertThat(orderView.getOrderLines()).extracting("menuId", "quantity", "price")
				.containsExactlyInAnyOrder(tuple(chickenId, 1, 20000), tuple(pizzaId, 2, 17000));

			// 호출 여부 확인 -> "실제로 리포지토리에 저장이 요청되었는가?"
			verify(orderRepository, times(1)).save(any(Order.class));
		}

		@Test
		@DisplayName("결제 실패 테스트")
		void createOrder_Payment() {
			//Stubbing
			// when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
			when(orderCartRequirer.getCartLines(cartId)).thenReturn(mockCartData);
			when(orderMenuRequirer.getMenus(menuIds)).thenReturn(menuData);
			when(orderPaymentRequirer.processPayment(any(), any())).thenThrow(
				new OrderException(OrderErrorCode.PAYMENT_FAILED));

			assertThatThrownBy(() -> orderService.createOrder(customerId, req)).isInstanceOf(OrderException.class)
				.hasMessageContaining("결제 실패")
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.PAYMENT_FAILED);

			// 예외가 터졌으므로 저장은 절대 호출되지 않아야 함
			verify(orderRepository, never()).save(any());
		}

		@Test
		@DisplayName("빈 cart 실패 테스트")
		void createOrder_Cart() {
			//Stubbing
			CartData emptyCartData = new CartData(storeId, new ArrayList<>());
			List<UUID> emptyMenuIds = new ArrayList<>();
			List<MenuData> emptyMenuData = new ArrayList<>();

			// when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
			when(orderCartRequirer.getCartLines(cartId)).thenThrow(new OrderException(OrderErrorCode.CART_EMPTY));

			assertThatThrownBy(() -> orderService.createOrder(customerId, req)).isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.CART_EMPTY);

			// 예외가 터졌으므로 저장은 절대 호출되지 않아야 함
			verify(orderRepository, never()).save(any());

		}

		@Test
		@DisplayName("빈 주소 실패 테스트")
		void createOrder_Address() {
			//null or 빈 문자열 왔다고 가정
			// when(orderAddressRequirer.getAddress(addressId)).thenThrow(
			// 	new OrderException(OrderErrorCode.ADDRESS_INVALID));

			assertThatThrownBy(() -> orderService.createOrder(customerId, req)).isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.ADDRESS_INVALID);

			// 예외가 터졌으므로 저장은 절대 호출되지 않아야 함
			verify(orderRepository, never()).save(any());

		}

		@Test
		@DisplayName("수량이 1개 미만 실패")
		void createOrder_InvalidQuantity() {
			UUID kimbap = UUID.randomUUID();
			mockCartData.cartItems().add(new CartItems(kimbap, 0));
			menuIds.add(kimbap);
			menuData.add(new MenuData(kimbap, "kimbap", 4000));

			// when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
			when(orderCartRequirer.getCartLines(cartId)).thenReturn(mockCartData);
			when(orderMenuRequirer.getMenus(menuIds)).thenReturn(menuData);

			assertThatThrownBy(() -> orderService.createOrder(customerId, req)).isInstanceOf(OrderException.class)
				.hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.INVALID_QUANTITY);
		}

		@Test
		@DisplayName("가격이 0원 미만 실패")
		void createOrder_InvalidPrice() {
			UUID kimbap = UUID.randomUUID();
			mockCartData.cartItems().add(new CartItems(kimbap, 1));
			menuIds.add(kimbap);
			menuData.add(new MenuData(kimbap, "kimbap", -1));

			// when(orderAddressRequirer.getAddress(addressId)).thenReturn("서울시 강남구");
			when(orderCartRequirer.getCartLines(cartId)).thenReturn(mockCartData);
			when(orderMenuRequirer.getMenus(menuIds)).thenReturn(menuData);

			assertThatThrownBy(() -> orderService.createOrder(customerId, req)).isInstanceOf(OrderException.class)
				.hasFieldOrPropertyWithValue("errorCode", OrderErrorCode.INVALID_PRICE);
		}
	}
}
