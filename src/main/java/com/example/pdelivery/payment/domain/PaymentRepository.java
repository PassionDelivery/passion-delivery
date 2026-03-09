package com.example.pdelivery.payment.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;

public interface PaymentRepository extends Repository<Payment, UUID> {
	Payment save(Payment payment);

	Optional<Payment> findById(UUID paymentId);

	Optional<Payment> findByOrderId(UUID orderId);
}

