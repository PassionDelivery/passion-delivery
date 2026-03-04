package com.example.pdelivery.review.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_review")
public class ReviewEntity extends BaseEntity {
	@Column(name = "order_id", nullable = false, unique = true)
	private UUID orderId;
	@Column(name = "customer_id", nullable = false)
	private UUID customerId;
	@Column(name = "store_id", nullable = false)
	private UUID storeId;
	@Embedded
	private Review review;
}
