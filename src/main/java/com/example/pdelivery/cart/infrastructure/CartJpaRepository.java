package com.example.pdelivery.cart.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.cart.domain.CartEntity;

public interface CartJpaRepository extends JpaRepository<CartEntity, UUID> {
}
