package com.example.pdelivery.review.infrastructure;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.order.domain.OrderStatus;
import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.review.application.CreateReviewRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReviewValidator {
	private final ReviewUserRequirer reviewUserRequirer;
	private final ReviewStoreRequirer reviewStoreRequirer;
	private final ReviewOrderRequirer reviewOrderRequirer;

	public void validate(UUID customerId, CreateReviewRequest reviewRequest) {
		if (!reviewUserRequirer.existsBy(customerId)) {
			throw new ReviewException(REVIEW_USER_NOT_FOUND);
		}
		if (!reviewStoreRequirer.existsBy(reviewRequest.storeId())) {
			throw new ReviewException(REVIEW_STORE_NOT_FOUND);
		}

		OrderData orderInfo = reviewOrderRequirer.getOrderInfo(reviewRequest.orderId());
		if (orderInfo == null) {
			throw new ReviewException(REVIEW_ORDER_NOT_FOUND);
		}
		if (orderInfo.orderStatus() != OrderStatus.COMPLETED) {
			throw new ReviewException(REVIEW_ORDER_INVALID_STATUS);
		}
	}
}
