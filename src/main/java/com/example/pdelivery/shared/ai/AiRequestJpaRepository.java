package com.example.pdelivery.shared.ai;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestJpaRepository extends JpaRepository<AiRequestEntity, UUID> {

	Slice<AiRequestEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
