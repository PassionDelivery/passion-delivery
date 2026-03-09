package com.example.pdelivery.store.presentation;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.store.application.CategoryService;
import com.example.pdelivery.store.presentation.dto.CategoryPageResponse;
import com.example.pdelivery.store.presentation.dto.CategoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

	private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);

	private final CategoryService categoryService;

	@Operation(security = {})
	@GetMapping
	public ResponseEntity<ApiResponse<CategoryPageResponse<CategoryResponse>>> getCategories(
		@RequestParam(required = false) String search,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		int normalizedSize = ALLOWED_SIZES.contains(pageable.getPageSize()) ? pageable.getPageSize() : 10;
		Pageable normalized = PageRequest.of(pageable.getPageNumber(), normalizedSize, pageable.getSort());
		CategoryPageResponse<CategoryResponse> response = categoryService.searchCategories(search, normalized);
		return ApiResponse.ok(response);
	}
}
