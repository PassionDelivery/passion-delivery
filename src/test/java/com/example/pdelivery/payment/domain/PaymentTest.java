package com.example.pdelivery.payment.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

@DisplayName("결제 엔티티 테스트")
class PaymentTest {

	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final PaymentProvider PROVIDER = PaymentProvider.TOSS;
	private static final PaymentMethod METHOD = PaymentMethod.CARD;
	private static final long AMOUNT = 10_000L;

	@Test
	@DisplayName("결제 생성 성공 - 결제 생성 시 기본 필드와 STATUS는 READY")
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

	@Nested
	@DisplayName("결제 승인")
	class MarkPaid {

		@Test
		@DisplayName("성공 - READY 상태에서 PAID로 변경")
		void markPaid_success() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);

			LocalDateTime approvedAt = LocalDateTime.now();
			payment.markPaid("test_key", approvedAt);

			assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
			assertThat(payment.getProviderPaymentKey()).isEqualTo("test_key");
			assertThat(payment.getApprovedAt()).isEqualTo(approvedAt);
		}

		@Test
		@DisplayName("실패 - 이미 PAID 상태")
		void markPaid_fail_whenAlreadyPaid() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);

			payment.markPaid("test_key", LocalDateTime.now());

			LocalDateTime approvedAt = LocalDateTime.now();

			assertThatThrownBy(() -> payment.markPaid("other_key", approvedAt))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.ALREADY_PAID);
		}
	}

	@Nested
	@DisplayName("결제 취소 테스트")
	class Cancel {

		@Test
		@DisplayName("취소 성공 - READY 상태")
		void cancel_success_whenReady() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);

			payment.cancel();

			assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
		}

		@Test
		@DisplayName("취소 성공 - PAID 상태")
		void cancel_success_whenPaid() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);
			payment.markPaid("test_key", LocalDateTime.now());

			payment.cancel();

			assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
		}

		@Test
		@DisplayName("취소 실패 - CANCELLED 상태")
		void cancel_fail_whenAlreadyCancelled() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);
			payment.cancel();

			assertThatThrownBy(payment::cancel)
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_STATUS_TRANSITION);
		}
	}
}
