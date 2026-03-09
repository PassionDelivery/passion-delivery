package com.example.pdelivery.order.infrastructure.required.payment;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.error.OrderErrorCode;
import com.example.pdelivery.order.error.OrderException;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.provider.PaymentProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OrderPaymentRequirerImpl implements OrderPaymentRequirer {
	private final PaymentProvider paymentProvider;

	// private boolean paymentOrderProvider = true;

	public boolean processPayment(UUID customerId, CreatePaymentRequest request) {
		if (paymentProvider.processPayment(customerId, request))
			return true;
		else
			throw new OrderException(OrderErrorCode.PAYMENT_FAILED);
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}

	public boolean cancelPaymentByOrder(UUID orderId) {
		//return paymentOrderProvider의 결제 성공/실패 로직
		if (paymentProvider.cancelPaymentByOrder(orderId))
			return true;
		else
			throw new OrderException(OrderErrorCode.PAYMENT_FAILED, "결제 취소 실패");
		/*
			TO DO:
			ex) http 통신 시 timeout check -> SocketTimeoutException
			//throw new OrderException(OrderErrorCode.PROVIDER_ERROR);
		 */
	}
}
