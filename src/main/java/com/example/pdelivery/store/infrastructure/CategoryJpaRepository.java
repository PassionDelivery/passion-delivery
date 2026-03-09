package com.example.pdelivery.store.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pdelivery.store.domain.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {
}
