package com.example.pdelivery.order.application;

import static com.example.pdelivery.shared.enums.OrderStatus.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;
import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

import jakarta.transaction.Transactional;

@SpringBootTest
public class OrderServiceIntegrationTest {
	@Autowired
	OrderService orderService;
	@Autowired
	OrderRepository orderRepository;

	private UUID storeId = UUID.randomUUID();

	@Nested
	@Transactional
	@DisplayName("주문 상태 변경 테스트")
	class OrderStatus {
		Order order1;
		Order order2;

		@BeforeEach
		void setUpOrderStatus() {
			UUID chicken = UUID.randomUUID();
			UUID pizza = UUID.randomUUID();
			UUID customerId = UUID.randomUUID();
			String address = "서울시 종로구 12-54";
			List<OrderLineVO> orderLineVOs1 = List.of(
				new OrderLineVO(pizza, "pizza", 2, 17000),
				new OrderLineVO(chicken, "chicken", 1, 20000)
			);
			List<OrderLineVO> orderLineVOs2 = List.of(
				new OrderLineVO(chicken, "chicken", 2, 20000)
			);

			order1 = Order.create(storeId, address, customerId, orderLineVOs1);
			order2 = Order.create(storeId, address, customerId, orderLineVOs2);

			orderRepository.save(order1);
			orderRepository.save(order2);
		}

		@Test
		@DisplayName("주문 취소 성공 test")
		public void cancelOrder_Success() {
			OrderRequest.OrderCancelRequest req = new OrderRequest.OrderCancelRequest("단순 변심");

			assertThat(order1.checkCancellatioin()).isFalse();
			orderService.cancelOrder(order1.getId(), req);

			Order findResult = orderRepository.findById(order1.getId()).get();
			assertThat(findResult.checkCancellatioin()).isTrue();
		}

		@Test
		@DisplayName("주문 취소 성공")
		public void cancelOrder_TimeoutBoundary() {
			OrderRequest.OrderCancelRequest req = new OrderRequest.OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusMinutes(4).minusSeconds(59));
			orderRepository.save(order1);

			assertThat(order1.checkCancellatioin()).isFalse();
			orderService.cancelOrder(order1.getId(), req);

			Order findResult = orderRepository.findById(order1.getId()).get();
			assertThat(findResult.checkCancellatioin()).isTrue();
		}

		@Test
		@DisplayName("주문 취소 실패 - 시간초과")
		public void cancelOrder_Timeout() {
			OrderRequest.OrderCancelRequest req = new OrderRequest.OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "createdAt", LocalDateTime.now().minusMinutes(5));
			orderRepository.save(order1);

			assertThatThrownBy(() -> orderService.cancelOrder(order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.CANCEL_TIMEOUT);
		}

		@Test
		@DisplayName("주문 취소 실패 - 중복 취소")
		public void cancelOrder_AlreadyCancel() {
			OrderRequest.OrderCancelRequest req = new OrderRequest.OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "status", CANCELLED);
			orderRepository.save(order1);

			assertThatThrownBy(() -> orderService.cancelOrder(order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.ALREADY_CANCELED);
		}

		@Test
		@DisplayName("주문 취소 실패 - 이미 수락 상태")
		public void cancelOrder_AlreadyAccepted() {
			OrderRequest.OrderCancelRequest req = new OrderRequest.OrderCancelRequest("단순 변심");

			ReflectionTestUtils.setField(order1, "status", ACCEPTED);
			orderRepository.save(order1);

			assertThatThrownBy(() -> orderService.cancelOrder(order1.getId(), req))
				.isInstanceOf(OrderException.class)
				.extracting("errorCode")
				.isEqualTo(OrderErrorCode.INVALID_CANCEL_STATUS);
		}
	}
}
