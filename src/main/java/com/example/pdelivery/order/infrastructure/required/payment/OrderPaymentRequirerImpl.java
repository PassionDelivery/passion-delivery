package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderPaymentRequirerImpl implements OrderPaymentRequirer {
	//private final PaymentOrderProvider paymentOrderProvider;

	private boolean paymentOrderProvider = true;

	public Boolean processPayment(UUID orderId, Integer amount) {
		//return paymentOrderProvider의 결제 성공/실패 로직
		if (paymentOrderProvider)
			return true;
		else
			throw new OrderException(OrderErrorCode.PAYMENT_FAILED);
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
