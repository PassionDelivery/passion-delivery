package com.example.pdelivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Review(@Column(name = "rating", nullable = false) Integer rating,
					 @Column(name = "content", nullable = false, length = 200) String content) {
	public Review {
		if (rating <= 1 || rating >= 5) {
			throw new IllegalArgumentException("평점은 1 ~ 5점 사이여야 합니다.");
		}
		if (content == null || content.isBlank() || content.length() > 200) {
			throw new IllegalArgumentException("리뷰 내용은 공백이 아니고 또는 200자 이내여야 합니다.");
		}
	}
}
