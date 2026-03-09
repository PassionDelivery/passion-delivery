package com.example.pdelivery.review.presentation.dto;

import java.util.UUID;

import com.example.pdelivery.review.domain.RatingStatEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RatingStatResponse(
	@JsonProperty("store_id")
	UUID storeId,
	@JsonProperty("avg_rating")
	double avgRating,
	@JsonProperty("review_cnt")
	int reviewCnt
) {
	public static RatingStatResponse from(RatingStatEntity entity) {
		return new RatingStatResponse(
			entity.getStoreId(),
			Math.round(entity.getAvgRating() * 10) / 10.0,
			entity.getReviewCnt()
		);
	}
}
