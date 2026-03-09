package com.example.pdelivery.review.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.review.domain.RatingStatEntity;

public interface RatingStatJpaRepository extends JpaRepository<RatingStatEntity, UUID> {

	Optional<RatingStatEntity> findByStoreId(UUID storeId);
}
