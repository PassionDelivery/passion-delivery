package com.example.pdelivery.payment.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("결제 생성 DTO 테스트")
class CreatePaymentRequestTest {

	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final long AMOUNT = 10_000L;

	private final Validator validator;

	CreatePaymentRequestTest() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

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
			CreatePaymentRequest request = new CreatePaymentRequest(
				null,
				STORE_ID,
				PaymentMethod.CARD,
				PaymentProvider.TOSS,
				AMOUNT
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("orderId"));
		}

		@Test
		@DisplayName("store id: null")
		void createPaymentRequest_fail_whenStoreIdNull() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID,
				null,
				PaymentMethod.CARD,
				PaymentProvider.TOSS,
				AMOUNT
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("storeId"));
		}

		@Test
		@DisplayName("amount: null")
		void createPaymentRequest_fail_whenAmountNull() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID,
				STORE_ID,
				PaymentMethod.CARD,
				PaymentProvider.TOSS,
				null
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("amount"));
		}

		@Test
		@DisplayName("amount: 0 이하")
		void createPaymentRequest_fail_whenAmountNotPositive() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID,
				STORE_ID,
				PaymentMethod.CARD,
				PaymentProvider.TOSS,
				0L
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("amount"));
		}

		@Test
		@DisplayName("paymentMethod: null")
		void createPaymentRequest_fail_whenPaymentMethodNull() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID,
				STORE_ID,
				null,
				PaymentProvider.TOSS,
				AMOUNT
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("paymentMethod"));
		}

		@Test
		@DisplayName("paymentProvider: null")
		void createPaymentRequest_fail_whenPaymentProviderNull() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID,
				STORE_ID,
				PaymentMethod.CARD,
				null,
				AMOUNT
			);

			Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

			assertThat(violations)
				.extracting(ConstraintViolation::getPropertyPath)
				.anyMatch(path -> path.toString().equals("paymentProvider"));
		}

	}
}
