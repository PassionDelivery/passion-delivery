package com.example.pdelivery.order.provided;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.order.application.provider.OrderInfo;
import com.example.pdelivery.order.application.provider.OrderProvider;
import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderLineVO;
import com.example.pdelivery.order.domain.OrderRepository;

@Transactional
@SpringBootTest
class OrderProviderImplTest {
	@Autowired
	OrderProvider orderProvider;
	@Autowired
	OrderRepository orderRepository;

	@Test
	void getOrderInfo() {
		UUID customerId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		UUID chicken = UUID.randomUUID();
		UUID pizza = UUID.randomUUID();
		String address = "서울시 종로구 12-54";
		List<OrderLineVO> orderLineVOs1 = List.of(
			new OrderLineVO(pizza, "pizza", 2, 17000),
			new OrderLineVO(chicken, "chicken", 1, 20000)
		);
		List<OrderLineVO> orderLineVOs2 = List.of(
			new OrderLineVO(chicken, "chicken", 2, 20000)
		);

		Order order1 = Order.create(storeId, address, customerId, orderLineVOs1);
		Order order2 = Order.create(storeId, address, customerId, orderLineVOs2);

		orderRepository.save(order1);
		orderRepository.save(order2);

		OrderInfo result1 = orderProvider.getOrderInfo(order1.getId()).get();
		OrderInfo result2 = orderProvider.getOrderInfo(order2.getId()).get();

		assertThat(result1.orderId()).isEqualTo(order1.getId());
		assertThat(result1.orderLines()).hasSize(2);
		assertThat(result1.totalPrice()).isEqualTo(54000);
		assertThat(result1.orderLines().get(1).quantity()).isEqualTo(1);
		assertThat(result1.orderLines().get(0).menuName()).isEqualTo("pizza");

		assertThat(result2.orderId()).isEqualTo(order2.getId());
		assertThat(result2.orderLines()).hasSize(1);
		assertThat(result2.totalPrice()).isEqualTo(40000);
		assertThat(result2.orderLines().get(0).menuName()).isEqualTo("chicken");
	}
}