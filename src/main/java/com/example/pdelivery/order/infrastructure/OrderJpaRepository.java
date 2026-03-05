package com.example.pdelivery.order.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.order.domain.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
}
