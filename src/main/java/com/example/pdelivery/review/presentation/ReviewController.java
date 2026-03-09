package com.example.pdelivery.review.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.review.application.CreateReviewRequest;
import com.example.pdelivery.review.application.ReviewService;
import com.example.pdelivery.review.presentation.dto.ReviewResponse;
import com.example.pdelivery.review.presentation.dto.StoreReviewResponse;
import com.example.pdelivery.review.presentation.dto.UpdateReviewRequest;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	// 리뷰 작성
	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
		@RequestBody @Valid CreateReviewRequest reviewRequest,
		@AuthenticationPrincipal AuthUser authUser
	) {
		var review = reviewService.createReview(authUser.userId(), reviewRequest);
		return ApiResponse.create(ReviewResponse.from(review));
	}

	// 가게 리뷰 목록 조회 (평균 평점 포함)
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ApiResponse<StoreReviewResponse>> getStoreReviews(
		@PathVariable UUID storeId,
		@PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable
	) {
		StoreReviewResponse response = reviewService.getStoreReviews(storeId, pageable);
		return ApiResponse.ok(response);
	}

	// 리뷰 수정
	@PatchMapping("/{reviewId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
		@PathVariable UUID reviewId,
		@RequestBody @Valid UpdateReviewRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		ReviewResponse response = reviewService.updateReview(authUser.userId(), reviewId, request);
		return ApiResponse.ok(response);
	}

	// 리뷰 삭제 (소프트 딜리트)
	@DeleteMapping("/{reviewId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Void> deleteReview(
		@PathVariable UUID reviewId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		reviewService.deleteReview(authUser.userId(), reviewId);
		return ResponseEntity.noContent().build();
	}
}
