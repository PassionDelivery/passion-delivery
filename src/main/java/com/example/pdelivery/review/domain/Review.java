package com.example.pdelivery.review.domain;

import static com.example.pdelivery.review.ReviewErrorCode.*;

import com.example.pdelivery.review.ReviewException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Review(@Column(name = "rating", nullable = false) Integer rating,
					 @Column(name = "content", nullable = false, length = 200) String content) {
	public Review {
		if (rating < 1 || rating > 5) {
			throw new ReviewException(REVIEW_RATING_BOUNDED_ERROR);
		}
		if (content == null || content.isBlank() || content.length() > 200) {
			throw new ReviewException(REVIEW_CONTENT_SIZE_ERROR);
		}
	}
}
