package com.example.pdelivery.order.infrastructure;

import org.springframework.stereotype.Repository;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderJpaPersistence implements OrderRepository {
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}
}
