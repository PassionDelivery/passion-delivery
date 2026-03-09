package com.example.pdelivery.order.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.pdelivery.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
	@Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
	Slice<Order> findAllByCustomerId(UUID customerId, Pageable pageable);

	@Query("SELECT o FROM Order o WHERE o.storeId = :storeId ORDER BY o.createdAt DESC")
	Slice<Order> findAllByStoreId(UUID storeId, Pageable pageable);
}
