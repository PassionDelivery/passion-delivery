package com.example.pdelivery.review.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_review")
public class ReviewEntity extends BaseEntity {

	private UUID orderId;
	private UUID customerId;
	private UUID storeId;
	@Embedded
	private Review review;
}
