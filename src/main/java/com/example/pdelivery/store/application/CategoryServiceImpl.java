package com.example.pdelivery.store.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.store.domain.CategoryEntity;
import com.example.pdelivery.store.infrastructure.CategoryJpaRepository;
import com.example.pdelivery.store.presentation.dto.CategoryPageResponse;
import com.example.pdelivery.store.presentation.dto.CategoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	@Transactional(readOnly = true)
	public CategoryPageResponse<CategoryResponse> searchCategories(String search, Pageable pageable) {
		Page<CategoryEntity> page = (search == null || search.isBlank())
			? categoryJpaRepository.findByDeletedAtIsNull(pageable)
			: categoryJpaRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(search, pageable);
		return CategoryPageResponse.of(page.map(CategoryResponse::from));
	}
}
