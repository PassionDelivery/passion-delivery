package com.example.pdelivery.review.infrastructure;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.UUID;

import com.example.pdelivery.order.application.provider.OrderInfo;
import com.example.pdelivery.order.application.provider.OrderProvider;
import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.shared.annotations.Requirer;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class ReviewOrderRequirerImpl implements ReviewOrderRequirer {

	private final OrderProvider orderProvider;

	@Override
	public OrderData getOrderInfo(UUID orderId) {
		OrderInfo orderInfo = orderProvider.getOrderInfo(orderId)
			.orElseThrow(() -> new ReviewException(REVIEW_ORDER_NOT_FOUND));

		return new OrderData(orderInfo.status());
	}
}
