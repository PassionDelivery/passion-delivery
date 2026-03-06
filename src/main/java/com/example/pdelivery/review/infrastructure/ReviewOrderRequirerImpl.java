package com.example.pdelivery.review.infrastructure;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.Optional;
import java.util.UUID;

import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.shared.Requirer;
import com.example.pdelivery.shared.enums.OrderStatus;

@Requirer
public class ReviewOrderRequirerImpl implements ReviewOrderRequirer {
	@Override
	public OrderData getOrderInfo(UUID orderId) {
		//todo: provider에서 받은 값으로 변경 필요
		Optional<OrderData> orderData = Optional.of(new OrderData(OrderStatus.COMPLETED));
		return orderData.orElseThrow(
			() -> new ReviewException(REVIEW_USER_NOT_FOUND)
		);
	}
}
