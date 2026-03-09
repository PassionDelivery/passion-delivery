package com.example.pdelivery.review.application;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.review.ReviewException;
import com.example.pdelivery.review.domain.RatingStatEntity;
import com.example.pdelivery.review.domain.RatingStatRepository;
import com.example.pdelivery.review.domain.ReviewEntity;
import com.example.pdelivery.review.domain.ReviewRepository;
import com.example.pdelivery.review.presentation.dto.ReviewResponse;
import com.example.pdelivery.review.presentation.dto.StoreReviewResponse;
import com.example.pdelivery.review.presentation.dto.UpdateReviewRequest;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final RatingStatRepository ratingStatRepository;
	private final ReviewValidator reviewValidator;

	@Override
	public ReviewEntity createReview(UUID customerId, CreateReviewRequest reviewRequest) {
		reviewValidator.validate(customerId, reviewRequest);

		ReviewEntity review = reviewRepository.save(ReviewEntity.createReview(customerId, reviewRequest));

		RatingStatEntity ratingStat = ratingStatRepository.findByStoreId(reviewRequest.storeId())
			.orElseGet(() -> RatingStatEntity.create(reviewRequest.storeId()));
		ratingStat.addRating(reviewRequest.rating());
		ratingStatRepository.save(ratingStat);

		return review;
	}

	@Override
	@Transactional(readOnly = true)
	public StoreReviewResponse getStoreReviews(UUID storeId, Pageable pageable) {
		Slice<ReviewEntity> slice = reviewRepository.findByStoreId(storeId, pageable);

		List<ReviewResponse> reviews = slice.getContent().stream()
			.map(ReviewResponse::from)
			.toList();

		RatingStatEntity ratingStat = ratingStatRepository.findByStoreId(storeId)
			.orElse(null);

		double avgRating = ratingStat != null ? Math.round(ratingStat.getAvgRating() * 10) / 10.0 : 0;
		int reviewCnt = ratingStat != null ? ratingStat.getReviewCnt() : 0;

		return new StoreReviewResponse(reviews, avgRating, reviewCnt, slice.hasNext());
	}

	@Override
	public ReviewResponse updateReview(UUID customerId, UUID reviewId, UpdateReviewRequest request) {
		ReviewEntity review = findReviewOrThrow(reviewId);
		validateOwner(review, customerId);

		int oldRating = review.getReview().rating();
		review.updateReview(request.rating(), request.content());

		RatingStatEntity ratingStat = getOrCreateRatingStat(review.getStoreId());
		ratingStat.updateRating(oldRating, request.rating());
		ratingStatRepository.save(ratingStat);

		return ReviewResponse.from(reviewRepository.save(review));
	}

	@Override
	public void deleteReview(UUID customerId, UUID reviewId) {
		ReviewEntity review = findReviewOrThrow(reviewId);
		validateOwner(review, customerId);

		int rating = review.getReview().rating();
		review.softDelete(customerId);

		RatingStatEntity ratingStat = getOrCreateRatingStat(review.getStoreId());
		ratingStat.removeRating(rating);
		ratingStatRepository.save(ratingStat);
	}

	private ReviewEntity findReviewOrThrow(UUID reviewId) {
		return reviewRepository.findById(reviewId)
			.filter(r -> r.getDeletedAt() == null)
			.orElseThrow(() -> new ReviewException(REVIEW_NOT_FOUND));
	}

	private void validateOwner(ReviewEntity review, UUID customerId) {
		if (!review.isOwnedBy(customerId)) {
			throw new ReviewException(REVIEW_NOT_OWNER);
		}
	}

	private RatingStatEntity getOrCreateRatingStat(UUID storeId) {
		return ratingStatRepository.findByStoreId(storeId)
			.orElseGet(() -> RatingStatEntity.create(storeId));
	}
}
