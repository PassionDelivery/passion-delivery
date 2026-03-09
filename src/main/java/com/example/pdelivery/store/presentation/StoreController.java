package com.example.pdelivery.store.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;
import com.example.pdelivery.store.application.StoreService;
import com.example.pdelivery.store.domain.StoreStatus;
import com.example.pdelivery.store.presentation.dto.StoreCreateRequest;
import com.example.pdelivery.store.presentation.dto.StoreDetailResponse;
import com.example.pdelivery.store.presentation.dto.StoreResponse;
import com.example.pdelivery.store.presentation.dto.StoreSearchResponse;
import com.example.pdelivery.store.presentation.dto.StoreStatusUpdateRequest;
import com.example.pdelivery.store.presentation.dto.StoreUpdateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

	private final StoreService storeService;

	@PostMapping
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<ApiResponse<StoreResponse>> createStore(
		@RequestBody StoreCreateRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		StoreResponse response = storeService.createStore(request, authUser.userId(), isManagerOrMaster());
		return ApiResponse.create(response);
	}

	@GetMapping("/{storeId}")
	public ResponseEntity<ApiResponse<StoreDetailResponse>> getStore(
		@PathVariable UUID storeId
	) {
		StoreDetailResponse response = storeService.getStore(storeId);
		return ApiResponse.ok(response);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<StoreSearchResponse>>> searchStores(
		@RequestParam(required = false) String search,
		@RequestParam(required = false) UUID categoryId,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<StoreSearchResponse> response = storeService.searchStores(search, categoryId, pageable);
		return ApiResponse.ok(response);
	}

	@GetMapping(params = "status")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<ApiResponse<PageResponse<StoreResponse>>> getStoresByStatus(
		@RequestParam StoreStatus status,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		PageResponse<StoreResponse> response = storeService.getStoresByStatus(status, pageable);
		return ApiResponse.ok(response);
	}

	@PatchMapping("/{storeId}/status")
	@PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
	public ResponseEntity<ApiResponse<StoreResponse>> updateStoreStatus(
		@PathVariable UUID storeId,
		@RequestBody StoreStatusUpdateRequest request
	) {
		StoreResponse response = storeService.updateStoreStatus(storeId, request);
		return ApiResponse.ok(response);
	}

	@PutMapping("/{storeId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
		@PathVariable UUID storeId,
		@RequestBody StoreUpdateRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		StoreResponse response = storeService.updateStore(storeId, request, authUser.userId(), isManagerOrMaster());
		return ApiResponse.ok(response);
	}

	@DeleteMapping("/{storeId}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
	public ResponseEntity<Void> deleteStore(
		@PathVariable UUID storeId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		storeService.deleteStore(storeId, authUser.userId(), isManagerOrMaster());
		return ResponseEntity.noContent().build();
	}

	private boolean isManagerOrMaster() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.anyMatch(auth -> auth.equals("ROLE_MANAGER") || auth.equals("ROLE_MASTER"));
	}
}
