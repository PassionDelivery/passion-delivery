package com.example.pdelivery.review.domain;

import static com.example.pdelivery.review.ReviewErrorCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.pdelivery.review.ReviewException;

class ReviewTest {

	@Test
	void review() {
		var review1 = new Review(1, "content");
		var review2 = new Review(3, "content");
		var review3 = new Review(5, "content");

		assertThat(review1).isNotNull();
		assertThat(review1.rating()).isEqualTo(1);
		assertThat(review1.content()).isEqualTo("content");

		assertThat(review2).isNotNull();
		assertThat(review3).isNotNull();
	}

	@Test
	void reviewFail() {
		var message1 = assertThatThrownBy(() -> new Review(10, "content"))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message1).isEqualTo(REVIEW_RATING_BOUNDED_ERROR.message());

		var message2 = assertThatThrownBy(() -> new Review(3, "c".repeat(201)))
			.isInstanceOf(ReviewException.class).actual().getMessage();

		assertThat(message2).isEqualTo(REVIEW_CONTENT_SIZE_ERROR.message());
	}
}