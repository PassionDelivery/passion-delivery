package com.example.pdelivery.order.domain;

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
	public void saveOrderTest() {
		Order order = new Order("address");
		OrderLine pizza = new OrderLine("pizza", 1, 20000);
		order.addOrderLines(pizza);

		orderRepository.save(order);
	}
}
