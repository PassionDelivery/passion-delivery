package com.example.pdelivery.review.presentation.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StoreReviewResponse(
	List<ReviewResponse> reviews,
	@JsonProperty("avg_rating")
	double avgRating,
	@JsonProperty("review_cnt")
	int reviewCnt,
	@JsonProperty("has_next")
	boolean hasNext
) {
}
