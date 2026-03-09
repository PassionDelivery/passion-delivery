package com.example.pdelivery.store.application;

import org.springframework.data.domain.Pageable;

import com.example.pdelivery.store.presentation.dto.CategoryPageResponse;
import com.example.pdelivery.store.presentation.dto.CategoryResponse;

public interface CategoryService {

	CategoryPageResponse<CategoryResponse> searchCategories(String search, Pageable pageable);
}
