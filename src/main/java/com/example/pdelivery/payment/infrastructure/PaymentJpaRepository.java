package com.example.pdelivery.payment.infrastructure;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentStatus;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

	Optional<Payment> findByOrderId(UUID orderId);

	@Query("""
		    select p
		    from Payment p
		    where (:storeId is null or p.storeId = :storeId)
		      and (:paymentStatus is null or p.paymentStatus = :paymentStatus)
		      and (:paymentProvider is null or p.paymentProvider = :paymentProvider)
		      and (:paymentMethod is null or p.paymentMethod = :paymentMethod)
		      and (:from is null or p.createdAt >= :from)
		      and (:to is null or p.createdAt <= :to)
		      and (:keywordPattern is null or :keywordPattern = '' or lower(p.providerPaymentKey) like :keywordPattern)
		""")
	Slice<Payment> search(
		@Param("storeId") UUID storeId,
		@Param("paymentStatus") PaymentStatus status,
		@Param("paymentProvider") PaymentProvider paymentProvider,
		@Param("paymentMethod") PaymentMethod paymentMethod,
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to,
		@Param("keywordPattern") String keyword,
		Pageable pageable
	);
}
