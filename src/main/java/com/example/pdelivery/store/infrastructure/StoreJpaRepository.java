package com.example.pdelivery.store.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pdelivery.store.domain.StoreEntity;
import com.example.pdelivery.store.domain.StoreStatus;

public interface StoreJpaRepository extends JpaRepository<StoreEntity, UUID> {

	@Query("select s from StoreEntity s where s.id = :id and s.deletedAt is null")
	Optional<StoreEntity> findByIdAndNotDeleted(@Param("id") UUID id);

	@Query("""
		select s from StoreEntity s
		where s.deletedAt is null
		  and s.status = 'APPROVED'
		  and (:keyword is null or s.store.name like concat('%', :keyword, '%'))
		  and (:categoryId is null or s.categoryId = :categoryId)
		order by s.createdAt desc
		""")
	Slice<StoreEntity> searchByNameAndCategory(
		@Param("keyword") String keyword,
		@Param("categoryId") UUID categoryId,
		Pageable pageable);

	@Query("""
		select s from StoreEntity s
		where s.deletedAt is null
		  and s.status = :status
		order by s.createdAt desc
		""")
	Slice<StoreEntity> findByStatus(@Param("status") StoreStatus status, Pageable pageable);
}
