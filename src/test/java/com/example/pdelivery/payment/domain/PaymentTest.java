package com.example.pdelivery.payment.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("결제 엔티티 테스트")
class PaymentTest {

	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final PaymentProvider PROVIDER = PaymentProvider.TOSS;
	private static final PaymentMethod METHOD = PaymentMethod.CARD;
	private static final long AMOUNT = 10_000L;

	@Test
	@DisplayName("결제 생성 시 기본 필드와 STATUS는 READY")
	void create_success() {
		Payment payment = Payment.create(ORDER_ID, STORE_ID, PROVIDER, METHOD, AMOUNT);

		assertThat(payment.getOrderId()).isEqualTo(ORDER_ID);
		assertThat(payment.getStoreId()).isEqualTo(STORE_ID);
		assertThat(payment.getPaymentProvider()).isEqualTo(PROVIDER);
		assertThat(payment.getPaymentMethod()).isEqualTo(METHOD);
		assertThat(payment.getAmount()).isEqualTo(AMOUNT);
		assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
		assertThat(payment.getApprovedAt()).isNull();
		assertThat(payment.getProviderPaymentKey()).isNull();

	}
}
