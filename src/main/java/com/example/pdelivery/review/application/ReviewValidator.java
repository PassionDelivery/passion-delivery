package com.example.pdelivery.review.application;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.review.infrastructure.OrderData;
import com.example.pdelivery.review.infrastructure.ReviewOrderRequirer;
import com.example.pdelivery.review.infrastructure.ReviewStoreRequirer;
import com.example.pdelivery.shared.enums.OrderStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewValidator {
	private final ReviewStoreRequirer reviewStoreRequirer;
	private final ReviewOrderRequirer reviewOrderRequirer;

	public void validate(UUID customerId, CreateReviewRequest reviewRequest) {
		if (!reviewStoreRequirer.existsBy(reviewRequest.storeId())) {
			throw new ReviewException(REVIEW_STORE_NOT_FOUND);
		}

		OrderData orderInfo = reviewOrderRequirer.getOrderInfo(reviewRequest.orderId());

		if (orderInfo != null && orderInfo.orderStatus() != OrderStatus.COMPLETED) {
			throw new ReviewException(REVIEW_ORDER_INVALID_STATUS);
		}
	}
}
