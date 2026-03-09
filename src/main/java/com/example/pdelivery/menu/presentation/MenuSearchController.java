package com.example.pdelivery.menu.presentation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.menu.application.MenuService;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuSearchController {

	private final MenuService menuService;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<PageResponse<MenuResponse>>> searchMenus(
		@RequestParam String keyword,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<MenuResponse> response = menuService.searchMenus(keyword, pageable);
		return ApiResponse.ok(response);
	}
}
