package com.example.pdelivery.order.presentation;

import static com.example.pdelivery.order.application.OrderRequest.*;
import static com.example.pdelivery.order.presentation.OrderResponse.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.example.pdelivery.order.application.OrderService;
import com.example.pdelivery.order.domain.Order;
import com.example.pdelivery.order.infrastructure.required.store.OrderStoreRequirer;
import com.example.pdelivery.shared.ApiResponse;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.enums.OrderStatus;
import com.example.pdelivery.shared.security.AuthUser;
import com.example.pdelivery.user.domain.entity.UserRole;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {
	private final OrderService orderService;
	private final OrderStoreRequirer orderStoreRequirer;

	@PostMapping
	public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(@AuthenticationPrincipal AuthUser authUser,
		@RequestBody OrderCreateRequest req) {
		Order order = orderService.createOrder(authUser.userId(), req);
		OrderCreateResponse response = order.toCreateResponse();
		return ApiResponse.create(response);
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse>> getOrdersByCustomer(@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Parameter Pageable pageable) {
		PageResponse res = orderService.getOrderItemsByCustomer(authUser.userId(), pageable);

		return ApiResponse.create(res);
	}

	@PreAuthorize("hasRole('OWNER') or hasRole('Manager')")
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ApiResponse<PageResponse>> getOrdersByStore(@AuthenticationPrincipal AuthUser authUser,
		@PathVariable(name = "storeId") UUID storeId,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) @Parameter Pageable pageable) {
		//store의 ownerid
		PageResponse res = orderService.getOrderItemsByStore(storeId, pageable);

		return ApiResponse.create(res);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OrderDataResponse>> getOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable UUID orderId) {
		Order order = orderService.getOrder(orderId);
		//TO DO: userId와 orderId의 customerId같은지 확인 필요

		OrderDataResponse res = order.toSummaryResponse();

		// role에 따라 response 다름
		if (authUser.role() == UserRole.CUSTOMER) {
			UUID storeId = order.getStoreId();
			res.updateStoreInfo(storeId, orderStoreRequirer.getStoreName(storeId));
		} else {
			res.updateUserId(order.getCustomerId());
		}
		return ApiResponse.create(res);
	}

	@PatchMapping("/{orderId}/cancel")
	public ResponseEntity<ApiResponse<OrderStatusResponse>> cancelOrder(@AuthenticationPrincipal AuthUser authUser,
		@PathVariable UUID orderId,
		@RequestBody OrderCancelRequest req) {
		//TO DO: userId와 orderId의 customerId같은지 확인 필요
		orderService.cancelOrder(orderId, req);

		return ApiResponse.create(new OrderStatusResponse(orderId, OrderStatus.CANCELLED));
	}

	@PreAuthorize("hasRole('OWNER') or hasRols('Manager')")
	@PatchMapping("/{orderId}/status")
	public ResponseEntity<ApiResponse<OrderStatusResponse>> changeOrderStatus(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable UUID orderId,
		@RequestBody OrderChangeStatusRequest req) {
		//TO DO: userId와 orderId의 customerId같은지 확인 필요
		orderService.changeStatusOrder(orderId, req);

		return ApiResponse.create(new OrderStatusResponse(orderId, req.orderStatus()));
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<ApiResponse<Void>> deleteOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable UUID orderId) {
		//TO DO: userId와 orderId의 customerId같은지 확인 필요
		orderService.deleteOrder(orderId);

		return ApiResponse.ok(null);
	}
}
