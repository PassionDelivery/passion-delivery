package com.example.pdelivery.payment.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.payment.application.PaymentService;
import com.example.pdelivery.payment.application.dto.CreatePaymentRequest;
import com.example.pdelivery.payment.application.dto.CreatePaymentResponse;
import com.example.pdelivery.payment.domain.PaymentMethod;
import com.example.pdelivery.payment.domain.PaymentProvider;
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

}
