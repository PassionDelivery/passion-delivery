package com.example.pdelivery.payment.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.domain.Payment;
import com.example.pdelivery.payment.domain.PaymentRepository;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentValidator paymentValidator;
	// private final PaymentOrderRequirer paymentOrderRequirer;

	@Transactional
	@Override
	public CreatePaymentResponse createPayment(UUID customerId, CreatePaymentRequest request) {
		paymentValidator.createValidate(request);

		if (customerId == null) {
			throw new PaymentException(PaymentErrorCode.INVALID_CUSTOMER);
		}

		// todo: 주문의 고객,가게,총액의 값과 일치하는지 확인

		// PaymentOrderSummary summary = paymentOrderRequirer.getOrderSummary(request.orderId());
		//
		// if (!summary.customerId().equals(customerId)) {
		// 	throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER_ACCESS);
		// }
		// if (!summary.storeId().equals(request.storeId())) {
		// 	throw new PaymentException(PaymentErrorCode.INVALID_STORE_ID);
		// }
		// if (summary.totalAmount() != request.amount()) {
		// 	throw new PaymentException(PaymentErrorCode.INVALID_AMOUNT);
		// }

		Payment payment = Payment.create(
			request.orderId(), request.storeId(), request.paymentProvider(), request.paymentMethod(), request.amount());
		return CreatePaymentResponse.from(paymentRepository.save(payment));
	}
}
