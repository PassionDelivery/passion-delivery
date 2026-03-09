package com.example.pdelivery.review.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.pdelivery.review.domain.RatingStatEntity;
import com.example.pdelivery.review.domain.RatingStatRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RatingStatJpaPersistence implements RatingStatRepository {

	private final RatingStatJpaRepository ratingStatJpaRepository;

	@Override
	public RatingStatEntity save(RatingStatEntity ratingStat) {
		return ratingStatJpaRepository.save(ratingStat);
	}

	@Override
	public Optional<RatingStatEntity> findByStoreId(UUID storeId) {
		return ratingStatJpaRepository.findByStoreId(storeId);
	}
}
