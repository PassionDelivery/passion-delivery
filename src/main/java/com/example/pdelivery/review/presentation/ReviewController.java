package com.example.pdelivery.review.presentation;

import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.review.application.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ReviewController {
	private final ReviewService reviewService;
}
