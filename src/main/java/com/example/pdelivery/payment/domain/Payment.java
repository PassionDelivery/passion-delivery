package com.example.pdelivery.payment.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;
import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
public class Payment extends BaseEntity {

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_provider", nullable = false, length = 20)
	private PaymentProvider paymentProvider;

	@Column(name = "provider_payment_key", length = 200, unique = true)
	private String providerPaymentKey;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 20)
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private long amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	private PaymentStatus paymentStatus = PaymentStatus.READY;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	private Payment(UUID orderId, UUID storeId, PaymentProvider paymentProvider, PaymentMethod paymentMethod,
		long amount) {
		this.orderId = orderId;
		this.storeId = storeId;
		this.paymentProvider = paymentProvider;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
	}

	public static Payment create(UUID orderId, UUID storeId, PaymentProvider paymentProvider,
		PaymentMethod paymentMethod, long amount) {
		if (orderId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_ORDER_ID);
		}
		if (storeId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_STORE_ID);
		}
		if (amount <= 0) {
			throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
		}
		if (paymentMethod == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
		}
		if (paymentProvider == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_PAYMENT_PROVIDER);
		}

		return new Payment(orderId, storeId, paymentProvider, paymentMethod, amount);
	}

	public void markPaid(String providerPaymentKey, LocalDateTime approvedAt) {
		if (this.paymentStatus != PaymentStatus.READY) {
			throw new PaymentException(PaymentErrorCode.INVALID_STATUS_TRANSITION,
				"READY 상태가 아닌 결제는 승인할 수 없습니다. (current=" + this.paymentStatus + ")");
		}
		this.providerPaymentKey = Objects.requireNonNull(providerPaymentKey, "providerPaymentKey is required");
		this.approvedAt = Objects.requireNonNull(approvedAt, "approvedAt is required");
		this.paymentStatus = PaymentStatus.PAID;
	}

	public void markFailed(String providerPaymentKey, LocalDateTime approvedAt) {
		if (this.paymentStatus == PaymentStatus.PAID) {
			throw new PaymentException(PaymentErrorCode.ALREADY_PAID,
				"PAID 상태인 결제는 실패 처리 할 수 없습니다. (current=" + this.paymentStatus + ")");
		}
		if (this.paymentStatus != PaymentStatus.READY) {
			throw new PaymentException(PaymentErrorCode.INVALID_STATUS_TRANSITION,
				"READY 상태가 아닌 결제는 실패 처리 할 수 없습니다. (current=" + this.paymentStatus + ")");
		}

		if (providerPaymentKey != null) {
			this.providerPaymentKey = providerPaymentKey;
		}

		if (approvedAt != null) {
			this.approvedAt = approvedAt;
		}
		this.paymentStatus = PaymentStatus.FAILED;
	}

}

