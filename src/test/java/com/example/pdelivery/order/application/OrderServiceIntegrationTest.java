package com.example.pdelivery.order.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;

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
	}
}
