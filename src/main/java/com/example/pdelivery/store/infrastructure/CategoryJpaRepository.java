package com.example.pdelivery.store.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.store.domain.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

	Page<CategoryEntity> findByNameContainingIgnoreCaseAndDeletedAtIsNull(String name, Pageable pageable);

	Page<CategoryEntity> findByDeletedAtIsNull(Pageable pageable);
}
