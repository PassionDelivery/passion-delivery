package com.example.pdelivery.review.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.review.domain.ReviewEntity;
import com.example.pdelivery.review.presentation.dto.ReviewResponse;
import com.example.pdelivery.review.presentation.dto.StoreReviewResponse;
import com.example.pdelivery.review.presentation.dto.UpdateReviewRequest;
import com.example.pdelivery.shared.PageResponse;

public interface ReviewService {

	ReviewEntity createReview(UUID customerId, CreateReviewRequest reviewRequest);

	StoreReviewResponse getStoreReviews(UUID storeId, Pageable pageable);

	PageResponse<ReviewResponse> getMyReviews(UUID customerId, Pageable pageable);

	ReviewResponse updateReview(UUID customerId, UUID reviewId, UpdateReviewRequest request);

	void deleteReview(UUID customerId, UUID reviewId);
}
