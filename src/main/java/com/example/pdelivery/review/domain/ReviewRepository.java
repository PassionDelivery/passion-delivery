package com.example.pdelivery.review.domain;

import java.util.UUID;

import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<ReviewEntity, UUID> {
	ReviewEntity save(ReviewEntity review);
}
