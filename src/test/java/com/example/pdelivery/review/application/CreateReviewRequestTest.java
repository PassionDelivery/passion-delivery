package com.example.pdelivery.review.application;

import static com.example.pdelivery.review.ReviewErrorCode.*;
import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.review.ReviewException;

class CreateReviewRequestTest {

	@Test
	void createReviewRequest() {
		var storeId = UUID.randomUUID();
		var orderId = UUID.randomUUID();
		var rating = 3;
		var content = "content";

		var request = new CreateReviewRequest(storeId, orderId, rating, content);

		assertThat(request).isNotNull();
	}

	@Test
	void createReviewRequestFail() {
		UUID storeId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		Integer rating = 3;
		String content = "content";

		var message1 = Assertions.assertThatThrownBy(() -> new CreateReviewRequest(null, orderId, rating, content))
			.isInstanceOf(ReviewException.class);
		assertThat(message1.actual().getMessage()).isEqualTo(REVIEW_VALIDATOR_STORE_ID.message());

		var message2 = Assertions.assertThatThrownBy(() -> new CreateReviewRequest(storeId, null, rating, content))
			.isInstanceOf(ReviewException.class);
		assertThat(message2.actual().getMessage()).isEqualTo(REVIEW_VALIDATOR_ORDER_ID.message());

		var message3 = Assertions.assertThatThrownBy(() -> new CreateReviewRequest(storeId, orderId, null, content))
			.isInstanceOf(ReviewException.class);
		assertThat(message3.actual().getMessage()).isEqualTo(REVIEW_VALIDATOR_RATING.message());

		var message4 = Assertions.assertThatThrownBy(() -> new CreateReviewRequest(storeId, orderId, rating, null))
			.isInstanceOf(ReviewException.class);
		assertThat(message4.actual().getMessage()).isEqualTo(REVIEW_VALIDATOR_CONTENT.message());
	}
}