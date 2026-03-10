package com.example.pdelivery.payment.infrastructure.required.order;

import java.util.UUID;

import com.example.pdelivery.order.application.provider.OrderInfo;
import com.example.pdelivery.order.application.provider.OrderProvider;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;
import com.example.pdelivery.shared.annotations.Requirer;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class PaymentOrderRequirerImpl implements PaymentOrderRequirer {

	private final OrderProvider orderProvider;

	@Override
	public PaymentOrderSummary getOrderSummary(UUID orderId) {
		OrderInfo orderInfo = orderProvider.getOrderInfo(orderId)
			.orElseThrow(() -> new PaymentException((PaymentErrorCode.INVALID_ORDER_ID)));

		return new PaymentOrderSummary(
			orderInfo.orderId(),
			orderInfo.customerId(),
			orderInfo.storeId(),
			orderInfo.totalPrice()
		);
	}
}
