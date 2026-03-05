package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.review.domain.ReviewEntity;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {
}
