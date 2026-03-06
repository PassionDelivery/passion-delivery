package com.example.pdelivery.order.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

public interface OrderRepository extends Repository<Order, UUID> {
	Order save(Order order);

	Optional<Order> findById(UUID orderId);
}
