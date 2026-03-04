package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderPaymentRequirerImpl implements OrderPaymentRequirer {
	//private final PaymentOrderProvider paymentOrderProvider;
	public Boolean processPayment(UUID orderId, Integer amount) {
		//return paymentOrderProvider의 결제 성공/실패 로직
		return true;
	}
}
