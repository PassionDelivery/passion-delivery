package com.example.pdelivery.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Review(@Column(name = "rating", nullable = false) Integer rating,
					 @Column(name = "content", nullable = false, length = 200) String content) {
}
