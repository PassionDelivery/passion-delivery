package com.example.pdelivery.review.application;

import org.springframework.stereotype.Service;

import com.example.pdelivery.review.infrastructure.ReviewJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
	private final ReviewJpaRepository reviewJpaRepository;
}
