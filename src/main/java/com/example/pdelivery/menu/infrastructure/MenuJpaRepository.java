package com.example.pdelivery.menu.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pdelivery.menu.domain.MenuEntity;

import jakarta.persistence.LockModeType;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, UUID> {

	@Query("select m from MenuEntity m where m.id = :menuId and m.storeId = :storeId and m.deletedAt is null")
	Optional<MenuEntity> findByIdAndStoreId(@Param("menuId") UUID menuId, @Param("storeId") UUID storeId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
		select m from MenuEntity m
		where m.id = :menuId
		  and m.storeId = :storeId
		  and m.deletedAt is null
		""")
	Optional<MenuEntity> findByIdAndStoreIdForUpdate(@Param("menuId") UUID menuId, @Param("storeId") UUID storeId);

	@Query("select m from MenuEntity m where m.storeId = :storeId and m.deletedAt is null order by m.createdAt desc")
	Slice<MenuEntity> findAllByStoreId(@Param("storeId") UUID storeId, Pageable pageable);

	@Query("select m from MenuEntity m where m.storeId = :storeId and m.deletedAt is null and m.menu.name like %:keyword% order by m.createdAt desc")
	Slice<MenuEntity> searchByStoreIdAndName(@Param("storeId") UUID storeId, @Param("keyword") String keyword, Pageable pageable);
}
