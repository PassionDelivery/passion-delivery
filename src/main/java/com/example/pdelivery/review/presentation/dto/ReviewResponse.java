package com.example.pdelivery.review.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.pdelivery.review.domain.ReviewEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ReviewResponse(
	@JsonProperty("review_id")
	UUID reviewId,
	@JsonProperty("order_id")
	UUID orderId,
	@JsonProperty("customer_id")
	UUID customerId,
	@JsonProperty("store_id")
	UUID storeId,
	Integer rating,
	String content,
	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
	public static ReviewResponse from(ReviewEntity entity) {
		return new ReviewResponse(
			entity.getId(),
			entity.getOrderId(),
			entity.getCustomerId(),
			entity.getStoreId(),
			entity.getReview().rating(),
			entity.getReview().content(),
			entity.getCreatedAt()
		);
	}
}
