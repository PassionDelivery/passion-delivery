package com.example.pdelivery.payment.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentRepository;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

@DisplayName("결제 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentValidator paymentValidator;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Nested
	@DisplayName("결제 생성 테스트")
	class CreatePayment {

		private static final UUID CUSTOMER_ID = UUID.randomUUID();
		private static final UUID ORDER_ID = UUID.randomUUID();
		private static final UUID STORE_ID = UUID.randomUUID();
		private static final PaymentProvider PROVIDER = PaymentProvider.TOSS;
		private static final PaymentMethod METHOD = PaymentMethod.CARD;
		private static final long AMOUNT = 15_000L;

		@Test
		@DisplayName("결제 생성 성공 - 성공 시 ID 반환")
		void createPayment_success() {

			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID, STORE_ID, METHOD, PROVIDER, AMOUNT);

			Payment savedPayment = Payment.create(request.orderId(), request.storeId(), request.paymentProvider(),
				request.paymentMethod(), request.amount());

			UUID paymentId = UUID.randomUUID();
			ReflectionTestUtils.setField(savedPayment, "id", paymentId);

			when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

			CreatePaymentResponse response = paymentService.createPayment(CUSTOMER_ID, request);

			verify(paymentValidator).createValidate(request);
			verify(paymentRepository).save(any(Payment.class));
			assertThat(response.paymentId()).isEqualTo(savedPayment.getId());
		}

		@Test
		@DisplayName("결제 생성 실패 - 유요하지 않는 결제 수단")
		void createPayment_fail_whenValidationFail() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID, STORE_ID, PaymentMethod.EASY_PAY, PROVIDER, AMOUNT
			);

			doThrow(new PaymentException(PaymentErrorCode.UNSUPPORTED_METHOD))
				.when(paymentValidator).createValidate(any(CreatePaymentRequest.class));

			assertThatThrownBy(() -> paymentService.createPayment(CUSTOMER_ID, request))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.UNSUPPORTED_METHOD);

			verify(paymentRepository, never()).save(any(Payment.class));
		}
	}

}
