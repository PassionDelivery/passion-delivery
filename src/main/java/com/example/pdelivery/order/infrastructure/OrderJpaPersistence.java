package com.example.pdelivery.order.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.domain.OrderRepository;

import lombok.RequiredArgsConstructor;

@Primary // 이후 다른 구현체 생성/사용 시 바꿀 수 있음
@Repository
@RequiredArgsConstructor
public class OrderJpaPersistence implements OrderRepository {
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}

	@Override
	public Optional<Order> findById(UUID orderId) {
		return orderJpaRepository.findById(orderId);
	}
}
