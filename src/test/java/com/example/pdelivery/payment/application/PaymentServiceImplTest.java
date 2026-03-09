package com.example.pdelivery.payment.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.pdelivery.payment.application.dto.ApprovePaymentResponse;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentSearchCondition;
import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentRepository;
import com.example.pdelivery.payment.domain.PaymentStatus;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;
import com.example.pdelivery.payment.infrastructure.PaymentJpaRepository;
import com.example.pdelivery.payment.infrastructure.required.order.PaymentOrderRequirerImpl;
import com.example.pdelivery.payment.infrastructure.required.order.PaymentOrderSummary;
import com.example.pdelivery.shared.PageResponse;

@DisplayName("결제 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentValidator paymentValidator;

	@Mock
	private PaymentOrderRequirerImpl paymentOrderRequirer;

	@Mock
	private PaymentJpaRepository paymentJpaRepository;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	private static final UUID PAYMENT_ID = UUID.randomUUID();
	private static final UUID CUSTOMER_ID = UUID.randomUUID();
	private static final UUID ORDER_ID = UUID.randomUUID();
	private static final UUID STORE_ID = UUID.randomUUID();
	private static final PaymentProvider PROVIDER = PaymentProvider.TOSS;
	private static final PaymentMethod METHOD = PaymentMethod.CARD;
	private static final long AMOUNT = 15_000L;

	@Nested
	@DisplayName("결제 생성 테스트")
	class CreatePayment {

		@Test
		@DisplayName("결제 생성 성공 - 성공 시 ID 반환")
		void createPayment_success() {

			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID, STORE_ID, METHOD, PROVIDER, AMOUNT);
			PaymentOrderSummary summary = new PaymentOrderSummary(
				ORDER_ID, CUSTOMER_ID, STORE_ID, AMOUNT);

			Payment savedPayment = Payment.create(request.orderId(), request.storeId(), request.paymentProvider(),
				request.paymentMethod(), request.amount());

			UUID paymentId = UUID.randomUUID();
			ReflectionTestUtils.setField(savedPayment, "id", paymentId);

			willDoNothing().given(paymentValidator).createValidate(request);
			given(paymentOrderRequirer.getOrderSummary(ORDER_ID)).willReturn(summary);
			given(paymentRepository.save(any(Payment.class))).willReturn(savedPayment);

			CreatePaymentResponse response = paymentService.createPayment(CUSTOMER_ID, request);

			verify(paymentValidator).createValidate(request);
			verify(paymentRepository).save(any(Payment.class));
			verify(paymentOrderRequirer).getOrderSummary(ORDER_ID);
			assertThat(response.paymentId()).isEqualTo(savedPayment.getId());
		}

		@Test
		@DisplayName("결제 생성 실패 - 유효하지 않는 결제 수단")
		void createPayment_fail_whenValidationFail() {
			CreatePaymentRequest request = new CreatePaymentRequest(
				ORDER_ID, STORE_ID, PaymentMethod.EASY_PAY, PROVIDER, AMOUNT
			);

			doThrow(new PaymentException(PaymentErrorCode.UNSUPPORTED_METHOD))
				.when(paymentValidator).createValidate(any(CreatePaymentRequest.class));

			assertThatThrownBy(() -> paymentService.createPayment(CUSTOMER_ID, request))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.UNSUPPORTED_METHOD);

			verify(paymentOrderRequirer, never()).getOrderSummary(any(UUID.class));
			verify(paymentRepository, never()).save(any(Payment.class));
		}
	}

	@Nested
	@DisplayName("결제 승인 테스트")
	class ApprovePayment {
		@Test
		@DisplayName("결제 승인 성공")
		void approvePayment_success() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);

			PaymentOrderSummary summary = new PaymentOrderSummary(
				ORDER_ID,
				CUSTOMER_ID,
				STORE_ID,
				AMOUNT
			);

			given(paymentOrderRequirer.getOrderSummary(ORDER_ID)).willReturn(summary);
			given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(payment));

			ApprovePaymentResponse response = paymentService.approvePayment(CUSTOMER_ID, PAYMENT_ID);

			assertThat(response.paymentStatus()).isEqualTo(PaymentStatus.PAID);
			assertThat(response.providerPaymentKey()).startsWith("test_");

		}

		@Test
		@DisplayName("결제 승인 실패 - 이미 결제 완료")
		void approvePayment_fail_whenAlreadyPaid() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				AMOUNT
			);

			PaymentOrderSummary summary = new PaymentOrderSummary(
				ORDER_ID,
				CUSTOMER_ID,
				STORE_ID,
				AMOUNT
			);

			payment.markPaid("test_existing_key", LocalDateTime.now());
			
			given(paymentOrderRequirer.getOrderSummary(ORDER_ID)).willReturn(summary);

			given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.of(payment));
			assertThatThrownBy(() -> paymentService.approvePayment(CUSTOMER_ID, PAYMENT_ID))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.ALREADY_PAID);
		}

		@Test
		@DisplayName("결제 승인 실패 - 결제 미존재")
		void approvePayment_fail_whenPaymentNotFound() {
			given(paymentRepository.findById(PAYMENT_ID)).willReturn(Optional.empty());
			assertThatThrownBy(() -> paymentService.approvePayment(CUSTOMER_ID, PAYMENT_ID))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("결제 단건 조회")
	class GetPayment {

		@Test
		@DisplayName("성공")
		void getPayment_success() {
			Payment payment = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				10_000L
			);

			ReflectionTestUtils.setField(payment, "id", PAYMENT_ID);
			payment.markPaid("test-payment-key", LocalDateTime.of(2026, 3, 9, 12, 0));

			given(paymentJpaRepository.findById(PAYMENT_ID))
				.willReturn(Optional.of(payment));

			PaymentResponse result = paymentService.getPayment(PAYMENT_ID);

			assertThat(result.paymentId()).isEqualTo(PAYMENT_ID);
			assertThat(result.orderId()).isEqualTo(ORDER_ID);
			assertThat(result.storeId()).isEqualTo(STORE_ID);
			assertThat(result.paymentProvider()).isEqualTo(PaymentProvider.TOSS);
			assertThat(result.paymentMethod()).isEqualTo(PaymentMethod.CARD);
			assertThat(result.amount()).isEqualTo(10_000L);
			assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.PAID);
			assertThat(result.providerPaymentKey()).isEqualTo("test-payment-key");
		}

		@Test
		@DisplayName("실패 - 존재하지 않는 결제")
		void getPayment_fail_whenPaymentNotFound() {
			given(paymentJpaRepository.findById(PAYMENT_ID))
				.willReturn(Optional.empty());

			assertThatThrownBy(() -> paymentService.getPayment(PAYMENT_ID))
				.isInstanceOf(PaymentException.class)
				.hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);
		}
	}

	@Nested
	@DisplayName("결제 목록 조회 테스트")
	class SearchPayments {
		@Test
		@DisplayName("결제 목록 조회 성공")
		void searchPayments_success() {
			PaymentSearchCondition condition = new PaymentSearchCondition(
				STORE_ID,
				null,
				PaymentStatus.PAID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				LocalDateTime.of(2026, 3, 1, 0, 0),
				LocalDateTime.of(2026, 3, 31, 23, 59),
				"test"
			);

			Pageable pageable = PageRequest.of(0, 10);

			Payment payment1 = Payment.create(
				ORDER_ID,
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				10_000L
			);
			ReflectionTestUtils.setField(payment1, "id", PAYMENT_ID);
			payment1.markPaid("test-payment-key-1", LocalDateTime.of(2026, 3, 9, 12, 0));

			Payment payment2 = Payment.create(
				UUID.randomUUID(),
				STORE_ID,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				20_000L
			);
			ReflectionTestUtils.setField(payment2, "id", UUID.randomUUID());
			payment2.markPaid("test-payment-key-2", LocalDateTime.of(2026, 3, 10, 12, 0));
			Slice<Payment> slice = new SliceImpl<>(List.of(payment1, payment2), pageable, false);
			given(paymentJpaRepository.search(
				eq(STORE_ID),
				eq(PaymentStatus.PAID),
				eq(PaymentProvider.TOSS),
				eq(PaymentMethod.CARD),
				eq(LocalDateTime.of(2026, 3, 1, 0, 0)),
				eq(LocalDateTime.of(2026, 3, 31, 23, 59)),
				eq("%test%"),
				eq(pageable)
			)).willReturn(slice);

			PageResponse<PaymentResponse> result = paymentService.search(null, condition, pageable);

			assertThat(result.contents()).hasSize(2);
			assertThat(result.hasNext()).isFalse();

			assertThat(result.contents().get(0).paymentId()).isEqualTo(PAYMENT_ID);
			assertThat(result.contents().get(0).orderId()).isEqualTo(ORDER_ID);
			assertThat(result.contents().get(0).storeId()).isEqualTo(STORE_ID);
			assertThat(result.contents().get(0).paymentProvider()).isEqualTo(PaymentProvider.TOSS);
			assertThat(result.contents().get(0).paymentMethod()).isEqualTo(PaymentMethod.CARD);
			assertThat(result.contents().get(0).paymentStatus()).isEqualTo(PaymentStatus.PAID);
			assertThat(result.contents().get(0).providerPaymentKey()).isEqualTo("test-payment-key-1");

			verify(paymentJpaRepository).search(
				eq(STORE_ID),
				eq(PaymentStatus.PAID),
				eq(PaymentProvider.TOSS),
				eq(PaymentMethod.CARD),
				eq(LocalDateTime.of(2026, 3, 1, 0, 0)),
				eq(LocalDateTime.of(2026, 3, 31, 23, 59)),
				eq("%test%"),
				eq(pageable)
			);
		}
	}
}


