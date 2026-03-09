package com.example.pdelivery.payment.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.payment.application.PaymentService;
import com.example.pdelivery.payment.application.dto.ApprovePaymentResponse;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentSearchCondition;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPayment(
		@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CreatePaymentRequest createPaymentRequest) {
		CreatePaymentResponse response = paymentService.createPayment(authUser.userId(), createPaymentRequest);
		return ApiResponse.create(response);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getPayments(
		@AuthenticationPrincipal AuthUser authUser, PaymentSearchCondition condition, @PageableDefault(size = 20)
		Pageable pageable) {
		return ApiResponse.ok(paymentService.search(authUser, condition, pageable));
	}

	@GetMapping("/{paymentId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','OWNER', 'MANAGER')")
	public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID paymentId) {
		return ApiResponse.ok(paymentService.getPayment(paymentId));
	}

	@PostMapping("/{paymentId}/approve")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<ApprovePaymentResponse>> approvePayment(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable UUID paymentId) {
		return ApiResponse.create(paymentService.approvePayment(authUser.userId(), paymentId));
	}
}
