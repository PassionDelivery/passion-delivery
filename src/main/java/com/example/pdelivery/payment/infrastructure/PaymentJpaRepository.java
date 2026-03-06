package com.example.pdelivery.payment.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
}
