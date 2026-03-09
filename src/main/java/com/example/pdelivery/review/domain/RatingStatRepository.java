package com.example.pdelivery.review.domain;

import java.util.Optional;
import java.util.UUID;

public interface RatingStatRepository {

	RatingStatEntity save(RatingStatEntity ratingStat);

	Optional<RatingStatEntity> findByStoreId(UUID storeId);
}
