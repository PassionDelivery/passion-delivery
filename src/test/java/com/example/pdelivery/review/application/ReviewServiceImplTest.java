package com.example.pdelivery.review.application;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.pdelivery.review.domain.ReviewRepository;
import com.example.pdelivery.review.infrastructure.ReviewValidator;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
	@InjectMocks
	ReviewServiceImpl reviewService;

	@Mock
	ReviewRepository reviewRepository;

	@Mock
	ReviewValidator reviewValidator;

	@Test
	void createReview() {
		var customerId = UUID.randomUUID();
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 5;
		var content = "content";
		var reviewRequest = new CreateReviewRequest(storeId, orderId, rating, content);

		reviewService.createReview(customerId, reviewRequest);

		verify(reviewValidator, times(1)).validate(customerId, reviewRequest);
		verify(reviewRepository, times(1)).save(any());
	}
}