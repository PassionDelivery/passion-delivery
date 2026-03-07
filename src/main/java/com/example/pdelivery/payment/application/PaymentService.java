package com.example.pdelivery.payment.application;

import java.util.UUID;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;

public interface PaymentService {

	CreatePaymentResponse createPayment(UUID customerId, CreatePaymentRequest request);
}
