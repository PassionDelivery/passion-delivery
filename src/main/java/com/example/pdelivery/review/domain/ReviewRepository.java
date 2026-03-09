package com.example.pdelivery.review.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepository {

	ReviewEntity save(ReviewEntity review);

	Optional<ReviewEntity> findById(UUID reviewId);

	Slice<ReviewEntity> findByStoreId(UUID storeId, Pageable pageable);

	Slice<ReviewEntity> findByCustomerId(UUID customerId, Pageable pageable);

	Slice<ReviewEntity> findByStoreIdIn(List<UUID> storeIds, Pageable pageable);
}
