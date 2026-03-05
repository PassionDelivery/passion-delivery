package com.example.pdelivery.review.domain;

import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<ReviewEntity, Long> {
	ReviewEntity save(ReviewEntity review);
}
