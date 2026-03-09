package com.example.pdelivery.cart.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.cart.application.CartService;
import com.example.pdelivery.cart.presentation.dto.CartAddItemRequest;
import com.example.pdelivery.cart.presentation.dto.CartResponse;
import com.example.pdelivery.cart.presentation.dto.CartUpdateItemRequest;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.security.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

	private final CartService cartService;

	// 장바구니 상품 목록 조회
	@GetMapping("/items")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<CartResponse>> getMyCartItems(
		@AuthenticationPrincipal AuthUser authUser
	) {
		CartResponse response = cartService.getMyCartItems(authUser.userId());
		return ApiResponse.ok(response);
	}

	// 장바구니에 상품 추가
	@PostMapping("/items")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<CartResponse>> addItem(
		@RequestBody @Valid CartAddItemRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		CartResponse response = cartService.addItem(authUser.userId(), request);
		return ApiResponse.create(response);
	}

	// 장바구니 상품 수량 수정
	@PatchMapping("/items/{itemId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<CartResponse>> updateItem(
		@PathVariable UUID itemId,
		@RequestBody @Valid CartUpdateItemRequest request,
		@AuthenticationPrincipal AuthUser authUser
	) {
		CartResponse response = cartService.updateItem(authUser.userId(), itemId, request);
		return ApiResponse.ok(response);
	}

	// 장바구니 상품 단건 삭제
	@DeleteMapping("/items/{itemId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Void> removeItem(
		@PathVariable UUID itemId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		cartService.removeItem(authUser.userId(), itemId);
		return ResponseEntity.noContent().build();
	}

	// 장바구니 비우기
	@DeleteMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<Void> clearCart(
		@AuthenticationPrincipal AuthUser authUser
	) {
		cartService.clearCart(authUser.userId());
		return ResponseEntity.noContent().build();
	}
}
