package com.example.pdelivery.review.infrastructure;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.review.domain.ReviewEntity;
import com.example.pdelivery.review.domain.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Primary
@Repository
@RequiredArgsConstructor
public class ReviewJpaPersistence implements ReviewRepository {
	private final ReviewJpaRepository reviewJpaRepository;

	@Override
	public ReviewEntity save(ReviewEntity review) {
		return reviewJpaRepository.save(review);
	}
}
