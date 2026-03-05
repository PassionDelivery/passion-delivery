package com.example.pdelivery.review.application;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.UUID;

import com.example.pdelivery.review.ReviewException;

public record CreateReviewRequest(UUID storeId, UUID orderId, Integer rating, String content) {
	public CreateReviewRequest {
		if (storeId == null) {
			throw new ReviewException(REVIEW_VALIDATOR_STORE_ID);
		}

		if (orderId == null) {
			throw new ReviewException(REVIEW_VALIDATOR_ORDER_ID);
		}

		if (rating == null || rating < 1 || rating > 5) {
			throw new ReviewException(REVIEW_VALIDATOR_RATING);
		}

		if (content == null || content.isBlank() || content.length() > 200) {
			throw new ReviewException(REVIEW_VALIDATOR_CONTENT);
		}
	}
}
