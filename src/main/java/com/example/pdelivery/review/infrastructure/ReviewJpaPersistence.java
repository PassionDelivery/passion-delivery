package com.example.pdelivery.review.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.example.pdelivery.review.domain.ReviewEntity;
import com.example.pdelivery.review.domain.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewJpaPersistence implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;

	@Override
	public ReviewEntity save(ReviewEntity review) {
		return reviewJpaRepository.save(review);
	}

	@Override
	public Optional<ReviewEntity> findById(UUID reviewId) {
		return reviewJpaRepository.findByIdAndDeletedAtIsNull(reviewId);
	}

	@Override
	public Slice<ReviewEntity> findByStoreId(UUID storeId, Pageable pageable) {
		return reviewJpaRepository.findByStoreIdAndDeletedAtIsNull(storeId, pageable);
	}

	@Override
	public Slice<ReviewEntity> findByCustomerId(UUID customerId, Pageable pageable) {
		return reviewJpaRepository.findByCustomerIdAndDeletedAtIsNull(customerId, pageable);
	}

	@Override
	public Slice<ReviewEntity> findByStoreIdIn(List<UUID> storeIds, Pageable pageable) {
		return reviewJpaRepository.findByStoreIdInAndDeletedAtIsNull(storeIds, pageable);
	}
}
