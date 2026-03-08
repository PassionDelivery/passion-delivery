package com.example.pdelivery.menu.presentation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.menu.application.MenuService;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionHistoryResponse;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionRequest;
import com.example.pdelivery.menu.presentation.dto.AiDescriptionResponse;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;
import com.example.pdelivery.menu.presentation.dto.MenuUpdateRequest;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}/menus")
public class MenuController {

	private final MenuService menuService;

	@GetMapping("/ai/history")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<List<AiDescriptionHistoryResponse>>> getAiDescriptionHistory(
		@AuthenticationPrincipal AuthUser authUser
	) {
		List<AiDescriptionHistoryResponse> response = menuService.getAiDescriptionHistory(authUser.userId());
		return ApiResponse.ok(response);
	}

	@PostMapping("/{menuId}/ai/description")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<AiDescriptionResponse>> generateAiDescription(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody @Valid AiDescriptionRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		AiDescriptionResponse response = menuService.generateAiDescription(
			storeId, menuId, authUser.userId(), request);
		return ApiResponse.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
		@PathVariable UUID storeId,
		@RequestBody @Valid MenuCreateRequest request
	) {
		MenuResponse response = menuService.createMenu(storeId, request);
		return ApiResponse.create(response);
	}

	@GetMapping("/{menuId}")
	public ResponseEntity<ApiResponse<MenuResponse>> getMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId
	) {
		MenuResponse response = menuService.getMenu(storeId, menuId);
		return ApiResponse.ok(response);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<MenuResponse>>> getMenus(
		@PathVariable UUID storeId,
		@RequestParam(required = false) String keyword,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<MenuResponse> response = menuService.getMenus(storeId, keyword, pageable);
		return ApiResponse.ok(response);
	}

	@PutMapping("/{menuId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@RequestBody @Valid MenuUpdateRequest request
	) {
		MenuResponse response = menuService.updateMenu(storeId, menuId, request);
		return ApiResponse.ok(response);
	}

	@DeleteMapping("/{menuId}")
	@PreAuthorize("hasRole('OWNER')")
	public ResponseEntity<Void> deleteMenu(
		@PathVariable UUID storeId,
		@PathVariable UUID menuId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		menuService.deleteMenu(storeId, menuId, authUser.userId());
		return ResponseEntity.noContent().build();
	}
}
