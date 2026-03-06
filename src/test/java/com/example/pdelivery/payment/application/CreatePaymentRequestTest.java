package com.example.pdelivery.payment.application;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

@DisplayName("결제 생성 DTO 테스트")
class CreatePaymentRequestTest {

	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final long AMOUNT = 10_000L;

	@Test
	@DisplayName("결제 생성 요청 DTO 생성 성공")
	void createPaymentRequest_success() throws OrderException {
		CreatePaymentRequest request = new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.CARD,
			PaymentProvider.TOSS, AMOUNT);

		assertThat(request.orderId()).isEqualTo(ORDER_ID);
		assertThat(request.storeId()).isEqualTo(STORE_ID);
		assertThat(request.amount()).isEqualTo(AMOUNT);
	}

	@Nested
	@DisplayName("결제 생성 요청 DTO 생성 실패")
	class createPaymentRequest_fail {
		@Test
		@DisplayName("order id: null")
		void createPaymentRequest_fail_whenOrderIdNull() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(null, STORE_ID, PaymentMethod.CARD, PaymentProvider.TOSS, AMOUNT))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_ORDER_ID);
		}

		@Test
		@DisplayName("store id: null")
		void createPaymentRequest_fail_whenStoreIdNull() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(ORDER_ID, null, PaymentMethod.CARD, PaymentProvider.TOSS, AMOUNT))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_STORE_ID);
		}

		@Test
		@DisplayName("amount: null")
		void createPaymentRequest_fail_whenAmountNull() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.CARD, PaymentProvider.TOSS, null))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_AMOUNT);
		}

		@Test
		@DisplayName("amount: 0 이하")
		void createPaymentRequest_fail_whenAmountNotPositive() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.CARD, PaymentProvider.TOSS, 0L))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_AMOUNT);
		}

		@Test
		@DisplayName("paymentMethod: null")
		void createPaymentRequest_fail_whenPaymentMethodNull() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(ORDER_ID, STORE_ID, null, PaymentProvider.TOSS, AMOUNT))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_METHOD);
		}

		@Test
		@DisplayName("paymentProvider: null")
		void createPaymentRequest_fail_whenPaymentProviderNull() {
			assertThatThrownBy(
				() -> new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.CARD, null, AMOUNT))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_PROVIDER);
		}

	}
}
