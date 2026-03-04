package com.example.pdelivery.review.infrastructure;

import com.example.pdelivery.review.domain.ReviewRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewJpaPersistence implements ReviewRepository {
	private final ReviewJpaRepository reviewJpaRepository;

}
