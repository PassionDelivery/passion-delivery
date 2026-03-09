package com.example.pdelivery.payment.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.payment.application.dto.ApprovePaymentResponse;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentSearchCondition;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;

public interface PaymentService {

	CreatePaymentResponse createPayment(UUID customerId, CreatePaymentRequest request);

	ApprovePaymentResponse approvePayment(UUID customerId, UUID paymentId);

	boolean approvePaymentByOrder(UUID customerId, CreatePaymentRequest request);

	PageResponse<PaymentResponse> search(AuthUser authUser, PaymentSearchCondition condition,
		Pageable pageable);

	PaymentResponse getPayment(UUID paymentId);

	boolean cancelPaymentByOrder(UUID orderId);
}
