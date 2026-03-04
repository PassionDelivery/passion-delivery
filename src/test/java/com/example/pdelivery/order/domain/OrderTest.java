package com.example.pdelivery.order.domain;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class OrderTest {
	@Autowired
	OrderRepository orderRepository;

	@Transactional
	// @Rollback(false)
	@Test
	public void saveDBTest() {
		Order order = null;
		OrderLineVO pizza = new OrderLineVO(UUID.randomUUID(), "pizza", 1, 20000);
		order = order.create(UUID.randomUUID(), "address", UUID.randomUUID(), List.of(pizza));

		orderRepository.save(order);
	}
}
