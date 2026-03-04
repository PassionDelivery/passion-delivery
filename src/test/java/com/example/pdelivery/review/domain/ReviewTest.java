package com.example.pdelivery.review.domain;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ReviewTest {

	@Test
	void review() {
		var review = new Review(3, "content");

		assertThat(review).isNotNull();
		assertThat(review.rating()).isEqualTo(3);
		assertThat(review.content()).isEqualTo("content");
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