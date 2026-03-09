package com.example.pdelivery.store.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.store.application.CategoryService;
import com.example.pdelivery.store.presentation.dto.CategoryPageResponse;
import com.example.pdelivery.store.presentation.dto.CategoryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<CategoryPageResponse<CategoryResponse>>> getCategories(
		@RequestParam(required = false) String search,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		CategoryPageResponse<CategoryResponse> response = categoryService.searchCategories(search, pageable);
		return ApiResponse.ok(response);
	}
}
