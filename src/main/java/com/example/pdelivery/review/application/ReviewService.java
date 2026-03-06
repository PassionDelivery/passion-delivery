package com.example.pdelivery.review.application;

import java.util.UUID;

import com.example.pdelivery.review.domain.ReviewEntity;

public interface ReviewService {
	ReviewEntity createReview(UUID customerId, CreateReviewRequest reviewRequest);
}
