package com.example.pdelivery.payment.application;

import org.springframework.stereotype.Component;

import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;

@Component
public class PaymentValidator {

	public void createValidate(CreatePaymentRequest request) {
		if (request.paymentMethod() != PaymentMethod.CARD) {
			throw new PaymentException(PaymentErrorCode.UNSUPPORTED_METHOD);
		}
	}
}
