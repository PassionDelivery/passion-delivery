package com.example.pdelivery.review.domain;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ReviewTest {

	@Test
	void review() {
		var review1 = new Review(1, "content");
		var review2 = new Review(3, "content");
		var review3 = new Review(5, "content");

		assertThat(review1).isNotNull();
		assertThat(review1.getRating()).isEqualTo(1);
		assertThat(review1.getContent()).isEqualTo("content");

		assertThat(review2).isNotNull();
		assertThat(review3).isNotNull();
	}

	@Test
	void reviewFail() {
		var message1 = Assertions.assertThatThrownBy(() -> {
			new Review(10, "content");
		}).isInstanceOf(IllegalArgumentException.class);

		assertThat(message1.actual().getMessage()).isEqualTo("평점은 1 ~ 5점 사이여야 합니다.");

		var message2 = Assertions.assertThatThrownBy(() -> {
			new Review(3, "c".repeat(201));
		}).isInstanceOf(IllegalArgumentException.class);

		assertThat(message2.actual().getMessage()).isEqualTo("리뷰 내용은 공백이 아니고 또는 200자 이내여야 합니다.");
	}
}