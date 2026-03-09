package com.example.pdelivery.payment.application;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.payment.application.dto.ApprovePaymentResponse;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentSearchCondition;
import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentRepository;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;
import com.example.pdelivery.payment.infrastructure.PaymentJpaRepository;
import com.example.pdelivery.payment.infrastructure.required.order.PaymentOrderRequirer;
import com.example.pdelivery.payment.infrastructure.required.order.PaymentOrderSummary;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentJpaRepository paymentJpaRepository;
	private final PaymentValidator paymentValidator;
	private final PaymentOrderRequirer paymentOrderRequirer;

	@Transactional
	@Override
	public CreatePaymentResponse createPayment(UUID customerId, CreatePaymentRequest request) {
		paymentValidator.createValidate(request);

		if (customerId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_CUSTOMER);
		}

		PaymentOrderSummary summary = paymentOrderRequirer.getOrderSummary(request.orderId());

		if (!summary.customerId().equals(customerId)) {
			throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER_ACCESS);
		}
		if (!summary.storeId().equals(request.storeId())) {
			throw new PaymentException(PaymentErrorCode.INVALID_STORE_ID);
		}
		if (request.amount() == null || summary.totalAmount() != request.amount()) {
			throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
		}

		Payment payment = Payment.create(
			request.orderId(), request.storeId(), request.paymentProvider(), request.paymentMethod(), request.amount());
		return CreatePaymentResponse.from(paymentRepository.save(payment));
	}

	// API를 통해 호출되는 승인 로직
	@Transactional
	@Override
	public ApprovePaymentResponse approvePayment(UUID customerId, UUID paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

		PaymentOrderSummary summary = paymentOrderRequirer.getOrderSummary(payment.getOrderId());

		if (!summary.customerId().equals(customerId)) {
			throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER_ACCESS);
		}

		String paymentKey = "test_" + UUID.randomUUID().toString();
		LocalDateTime approvedAt = LocalDateTime.now();

		payment.markPaid(paymentKey, approvedAt);
		return new ApprovePaymentResponse(
			payment.getId(),
			payment.getProviderPaymentKey(),
			payment.getPaymentStatus(),
			payment.getApprovedAt()
		);
	}

	// Order에서 사용하는 승인 로직
	@Override
	@Transactional
	public boolean approvePaymentByOrder(UUID orderId, Long amount) {
		Payment payment = paymentRepository.findByOrderId(orderId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
		if (amount == null || payment.getAmount() != amount) {
			throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
		}

		String providerPaymentKey = "test_" + UUID.randomUUID().toString();
		LocalDateTime approvedAt = LocalDateTime.now();
		payment.markPaid(providerPaymentKey, approvedAt);

		return true;
	}

	@Override
	@Transactional
	public void cancelPaymentByOrder(UUID orderId) {
		Payment payment = paymentRepository.findByOrderId(orderId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

		payment.cancel();
	}

	@Transactional(readOnly = true)
	@Override
	public PageResponse<PaymentResponse> search(
		AuthUser authUser,
		PaymentSearchCondition condition,
		Pageable pageable
	) {
		PaymentSearchCondition resolvedCondition = resolveCondition(authUser, condition);
		String keywordPattern = toKeywordPattern(resolvedCondition.keyword());

		Slice<Payment> page = paymentJpaRepository.search(
			resolvedCondition.storeId(),
			resolvedCondition.paymentStatus(),
			resolvedCondition.paymentProvider(),
			resolvedCondition.paymentMethod(),
			resolvedCondition.from(),
			resolvedCondition.to(),
			keywordPattern,
			pageable
		);

		Slice<PaymentResponse> responsePage = page.map(PaymentResponse::from);
		return PageResponse.of(responsePage);
	}

	@Transactional(readOnly = true)
	@Override
	public PaymentResponse getPayment(UUID paymentId) {
		Payment payment = paymentJpaRepository.findById(paymentId)
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
		return PaymentResponse.from(payment);
	}

	private PaymentSearchCondition resolveCondition(AuthUser authUser, PaymentSearchCondition condition) {
		// TODO: AUTHUSER 필드에 ROLE 넣어서 ROLE 체크 후 가게 주인이면 해당 가게로 가게 ID로 고정
		// if (authUser.isOwner()) {
		// 	UUID ownerStoreId = paymentStoreRequirer.getOwnerStoreId();
		// 	return new PaymentSearchCondition(
		// 		ownerStoreId,
		// 		condition.customerId(),
		// 		condition.status(),
		// 		condition.provider(),
		// 		condition.method(),
		// 		condition.from(),
		// 		condition.to(),
		// 		condition.keyword()
		// 	);
		// }
		return condition;
	}

	private String toKeywordPattern(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return "%" + keyword.toLowerCase() + "%";
	}
}
