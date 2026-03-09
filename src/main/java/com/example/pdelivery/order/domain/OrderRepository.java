package com.example.pdelivery.order.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;

public interface OrderRepository extends Repository<Order, UUID> {
	Order save(Order order);

	Optional<Order> findById(UUID orderId);

	Slice<Order> findAllByCustomerId(UUID customerId, Pageable pageable);

	Slice<Order> findAllByStoreId(UUID storeId, Pageable pageable);
}
