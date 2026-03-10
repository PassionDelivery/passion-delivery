package com.example.pdelivery.review.domain;

import java.util.UUID;

import com.example.pdelivery.review.application.CreateReviewRequest;
import com.example.pdelivery.shared.jpa.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity extends BaseEntity {
	@Column(name = "order_id", nullable = false, unique = true)
	private UUID orderId;
	@Column(name = "customer_id", nullable = false)
	private UUID customerId;
	@Column(name = "store_id", nullable = false)
	private UUID storeId;
	@Embedded
	private Review review;

	public static ReviewEntity createReview(UUID customerId, CreateReviewRequest reviewRequest) {
		ReviewEntity reviewEntity = new ReviewEntity();

		reviewEntity.orderId = reviewRequest.orderId();
		reviewEntity.customerId = customerId;
		reviewEntity.storeId = reviewRequest.storeId();
		reviewEntity.review = new Review(reviewRequest.rating(), reviewRequest.content());

		return reviewEntity;
	}

	public void updateReview(int rating, String content) {
		this.review = new Review(rating, content);
	}

	public boolean isOwnedBy(UUID customerId) {
		return this.customerId.equals(customerId);
	}
}
