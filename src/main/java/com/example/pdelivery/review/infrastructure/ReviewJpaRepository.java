package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.review.domain.ReviewEntity;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {

	Slice<ReviewEntity> findByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);
}
