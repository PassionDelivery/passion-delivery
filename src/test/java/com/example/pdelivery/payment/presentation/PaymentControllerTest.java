package com.example.pdelivery.payment.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.payment.application.PaymentService;
import com.example.pdelivery.payment.application.dto.ApprovePaymentResponse;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentResponse;
import com.example.pdelivery.payment.application.dto.PaymentSearchCondition;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
import com.example.pdelivery.payment.domain.PaymentStatus;
import com.example.pdelivery.payment.error.PaymentErrorCode;
import com.example.pdelivery.payment.error.PaymentException;
import com.example.pdelivery.shared.PageResponse;
import com.example.pdelivery.shared.security.AuthUser;
import com.example.pdelivery.shared.security.JwtAccessDeniedHandler;
import com.example.pdelivery.shared.security.JwtAuthFilter;
import com.example.pdelivery.shared.security.JwtAuthenticationEntryPoint;
import com.example.pdelivery.shared.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
@DisplayName("결제 controller 테스트")
public class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PaymentService paymentService;

	@MockitoBean
	JwtAuthFilter jwtAuthFilter;

	@MockitoBean
	JwtAuthenticationEntryPoint authEntryPoint;

	@MockitoBean
	JwtAccessDeniedHandler accessDeniedHandler;

	@BeforeEach
	void setUpFilterPassThrough() throws ServletException, IOException {
		// JwtAuthFilter is mocked — configure it to pass requests through to the actual controller
		doAnswer(inv -> {
			ServletRequest req = inv.getArgument(0);
			ServletResponse res = inv.getArgument(1);
			FilterChain chain = inv.getArgument(2);
			chain.doFilter(req, res);
			return null;
		}).when(jwtAuthFilter).doFilter(any(), any(), any());
	}

	private Authentication customerAuth(UUID userId, String username) {
		return new UsernamePasswordAuthenticationToken(
			new AuthUser(userId, username), null,
			List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
		);
	}

	private Authentication ownerAuth(UUID userId, String username) {
		return new UsernamePasswordAuthenticationToken(
			new AuthUser(userId, username), null,
			List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
		);
	}

	@Nested
	@DisplayName("결제 생성 테스트")
	class createPayment {

		@Test
		@DisplayName("결제 생성 성공")
		void createPayment_success() throws Exception {
			UUID customerId = UUID.randomUUID();
			UUID orderId = UUID.randomUUID();
			UUID storeId = UUID.randomUUID();
			UUID paymentId = UUID.randomUUID();

			CreatePaymentRequest request = new CreatePaymentRequest(orderId, storeId, PaymentMethod.CARD,
				PaymentProvider.TOSS, 15000L);

			CreatePaymentResponse response = new CreatePaymentResponse(paymentId);

			when(paymentService.createPayment(eq(customerId), any(CreatePaymentRequest.class))).thenReturn(response);

			mockMvc.perform(post("/api/payments")
					.with(csrf())
					.with(authentication(customerAuth(customerId, "customer@test.com")))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
		}

		@Test
		@DisplayName("결제 생성 실패 - 권한 없음")
		void createPayment_forbidden() throws Exception {
			CreatePaymentRequest request = new CreatePaymentRequest(UUID.randomUUID(), UUID.randomUUID(),
				PaymentMethod.CARD, PaymentProvider.TOSS, 15000L);

			mockMvc.perform(post("/api/payments")
					.with(csrf())
					.with(authentication(ownerAuth(UUID.randomUUID(), "owner@test.com")))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("결제 단건 조회 테스트")
	class GetPayment {

		@Test
		@DisplayName("결제 단건 조회 성공 - OWNER")
		void getPayment_success_owner() throws Exception {
			UUID ownerId = UUID.randomUUID();
			UUID paymentId = UUID.randomUUID();
			UUID orderId = UUID.randomUUID();
			UUID storeId = UUID.randomUUID();

			PaymentResponse response = new PaymentResponse(
				paymentId,
				orderId,
				storeId,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				15000L,
				PaymentStatus.PAID,
				"test-payment-key",
				LocalDateTime.of(2026, 3, 9, 12, 0),
				LocalDateTime.of(2026, 3, 9, 11, 50),
				LocalDateTime.of(2026, 3, 9, 12, 0)
			);

			when(paymentService.getPayment(paymentId)).thenReturn(response);

			mockMvc.perform(get("/api/payments/{paymentId}", paymentId)
					.with(csrf())
					.with(authentication(ownerAuth(ownerId, "owner@test.com"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.paymentId").value(paymentId.toString()))
				.andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
				.andExpect(jsonPath("$.data.storeId").value(storeId.toString()))
				.andExpect(jsonPath("$.data.paymentProvider").value("TOSS"))
				.andExpect(jsonPath("$.data.paymentMethod").value("CARD"))
				.andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
				.andExpect(jsonPath("$.data.providerPaymentKey").value("test-payment-key"));
		}

		@Test
		@DisplayName("결제 단건 조회 실패 - 존재하지 않는 결제")
		void getPayment_notFound() throws Exception {
			UUID paymentId = UUID.randomUUID();

			when(paymentService.getPayment(paymentId))
				.thenThrow(new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

			mockMvc.perform(get("/api/payments/{paymentId}", paymentId)
					.with(csrf())
					.with(authentication(customerAuth(UUID.randomUUID(), "customer@test.com"))))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("결제 목록 조회 테스트")
	class SearchPayments {

		@Test
		@DisplayName("결제 목록 조회 성공 - OWNER")
		void getPayments_success_owner() throws Exception {
			UUID ownerId = UUID.randomUUID();
			UUID paymentId = UUID.randomUUID();
			UUID orderId = UUID.randomUUID();
			UUID storeId = UUID.randomUUID();

			PaymentResponse paymentResponse = new PaymentResponse(
				paymentId,
				orderId,
				storeId,
				PaymentProvider.TOSS,
				PaymentMethod.CARD,
				15000L,
				PaymentStatus.PAID,
				"test-payment-key",
				LocalDateTime.of(2026, 3, 9, 12, 0),
				LocalDateTime.of(2026, 3, 9, 11, 50),
				LocalDateTime.of(2026, 3, 9, 12, 0)
			);

			PageResponse<PaymentResponse> response = new PageResponse<>(
				List.of(paymentResponse),
				false
			);

			when(paymentService.search(any(AuthUser.class), any(PaymentSearchCondition.class), any(Pageable.class)))
				.thenReturn(response);

			mockMvc.perform(get("/api/payments")
					.with(csrf())
					.with(authentication(ownerAuth(ownerId, "owner@test.com")))
					.param("storeId", storeId.toString())
					.param("status", "PAID")
					.param("paymentProvider", "TOSS")
					.param("paymentMethod", "CARD")
					.param("keyword", "test"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.contents[0].paymentId").value(paymentId.toString()))
				.andExpect(jsonPath("$.data.contents[0].orderId").value(orderId.toString()))
				.andExpect(jsonPath("$.data.contents[0].storeId").value(storeId.toString()))
				.andExpect(jsonPath("$.data.contents[0].paymentProvider").value("TOSS"))
				.andExpect(jsonPath("$.data.contents[0].paymentMethod").value("CARD"))
				.andExpect(jsonPath("$.data.contents[0].paymentStatus").value("PAID"))
				.andExpect(jsonPath("$.data.contents[0].providerPaymentKey").value("test-payment-key"))
				.andExpect(jsonPath("$.data.hasNext").value(false));
		}

		@Test
		@DisplayName("결제 목록 조회 실패 - CUSTOMER 권한 없음")
		void getPayments_forbidden_customer() throws Exception {
			mockMvc.perform(get("/api/payments")
					.with(csrf())
					.with(authentication(customerAuth(UUID.randomUUID(), "customer@test.com"))))
				.andExpect(status().isForbidden());
		}
	}

	@Nested
	@DisplayName("결제 승인 테스트")
	class ApprovePayment {

		@Test
		@DisplayName("결제 승인 성공")
		void approvePayment_success() throws Exception {
			UUID customerId = UUID.randomUUID();
			UUID paymentId = UUID.randomUUID();

			ApprovePaymentResponse response = new ApprovePaymentResponse(
				paymentId,
				"test_" + UUID.randomUUID(),
				PaymentStatus.PAID,
				LocalDateTime.of(2026, 3, 9, 12, 0)
			);

			when(paymentService.approvePayment(customerId, paymentId)).thenReturn(response);

			mockMvc.perform(post("/api/payments/{paymentId}/approve", paymentId)
					.with(csrf())
					.with(authentication(customerAuth(customerId, "customer@test.com"))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.paymentId").value(paymentId.toString()))
				.andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
				.andExpect(jsonPath("$.data.providerPaymentKey").value(response.providerPaymentKey()))
				.andExpect(jsonPath("$.data.approvedAt").value("2026-03-09T12:00:00"));
		}

		@Test
		@DisplayName("결제 승인 실패 - OWNER 권한 없음")
		void approvePayment_forbidden_owner() throws Exception {
			UUID paymentId = UUID.randomUUID();

			mockMvc.perform(post("/api/payments/{paymentId}/approve", paymentId)
					.with(csrf())
					.with(authentication(ownerAuth(UUID.randomUUID(), "owner@test.com"))))
				.andExpect(status().isForbidden());
		}

		@Test
		@DisplayName("결제 승인 실패 - 존재하지 않는 결제")
		void approvePayment_notFound() throws Exception {
			UUID customerId = UUID.randomUUID();
			UUID paymentId = UUID.randomUUID();

			when(paymentService.approvePayment(customerId, paymentId))
				.thenThrow(new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

			mockMvc.perform(post("/api/payments/{paymentId}/approve", paymentId)
					.with(csrf())
					.with(authentication(customerAuth(customerId, "customer@test.com"))))
				.andExpect(status().isNotFound());
		}
	}

}
