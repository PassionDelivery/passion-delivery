package com.example.pdelivery.review.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepository {

	ReviewEntity save(ReviewEntity review);

	Optional<ReviewEntity> findById(UUID reviewId);

	Slice<ReviewEntity> findByStoreId(UUID storeId, Pageable pageable);
}
