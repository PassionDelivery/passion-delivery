package com.example.pdelivery.review.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.review.domain.ReviewEntity;
import com.example.pdelivery.review.domain.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final ReviewValidator reviewValidator;

	@Override
	public ReviewEntity createReview(UUID customerId, CreateReviewRequest reviewRequest) {
		reviewValidator.validate(customerId, reviewRequest);

		return reviewRepository.save(ReviewEntity.createReview(customerId, reviewRequest));
	}
}
