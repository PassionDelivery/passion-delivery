package com.example.pdelivery.payment.application;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

@DisplayName("결제 validator 테스트")
class PaymentValidatorTest {

	private PaymentValidator validator = new PaymentValidator();

	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final long AMOUNT = 10_000L;

	@Test
	@DisplayName("성공 - 카드 결제만 통과")
	void validate_success_whenCard() {
		CreatePaymentRequest request = new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.CARD,
			PaymentProvider.TOSS, AMOUNT);
		assertThatCode(() -> validator.createValidate(request)).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("실패 - 지원하지 않는 결제 수단이면 예외")
	void validate_success_whenUnsupportedMethod() {
		CreatePaymentRequest request = new CreatePaymentRequest(ORDER_ID, STORE_ID, PaymentMethod.EASY_PAY,
			PaymentProvider.TOSS, AMOUNT);
		assertThatThrownBy(() -> validator.createValidate(request))
			.isInstanceOf(PaymentException.class)
			.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.UNSUPPORTED_METHOD);
	}

}
