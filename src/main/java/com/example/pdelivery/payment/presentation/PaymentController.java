package com.example.pdelivery.payment.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.payment.application.PaymentService;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.shared.ApiResponse;
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
}
