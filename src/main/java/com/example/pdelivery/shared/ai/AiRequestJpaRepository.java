package com.example.pdelivery.shared.ai;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiRequestJpaRepository extends JpaRepository<AiRequestEntity, UUID> {

	@Query("SELECT a FROM AiRequestEntity a WHERE a.userId = :userId ORDER BY a.createdAt DESC LIMIT :limit")
	List<AiRequestEntity> findRecentByUserId(@Param("userId") UUID userId, @Param("limit") int limit);
}
