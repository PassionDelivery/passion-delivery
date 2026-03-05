package com.example.pdelivery.review.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.review.application.CreateReviewRequest;
import com.example.pdelivery.review.application.ReviewService;
import com.example.pdelivery.shared.ApiResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ReviewController {
	private final ReviewService reviewService;

	public ResponseEntity<ApiResponse<UUID>> createReview(@RequestBody CreateReviewRequest reviewRequest) {
		//todo 인증객체로 수정 필요
		var customerId = UUID.randomUUID();
		var review = reviewService.createReview(customerId, reviewRequest);

		return ApiResponse.of(review.getId(), HttpStatus.CREATED);
	}
}
