package com.example.pdelivery.review.application;

import static org.mockito.BDDMockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.review.domain.ReviewRepository;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 서비스 테스트")
class ReviewServiceImplTest {
	@InjectMocks
	ReviewServiceImpl reviewService;

	@Mock
	ReviewRepository reviewRepository;

	@Mock
	ReviewValidator reviewValidator;

	@Test
	@DisplayName("리뷰 등록하기")
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