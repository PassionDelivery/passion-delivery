package com.example.pdelivery.payment.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Primary
@Repository
@RequiredArgsConstructor
public class PaymentPersistence implements PaymentRepository {

	private final PaymentJpaRepository paymentJpaRepository;

	@Override
	public Payment save(Payment payment) {
		return paymentJpaRepository.save(payment);
	}

	@Override
	public Optional<Payment> findById(UUID paymentId) {
		return paymentJpaRepository.findById(paymentId);
	}

	@Override
	public Optional<Payment> findByOrderId(UUID orderId) {
		return paymentJpaRepository.findByOrderId(orderId);
	}
}
