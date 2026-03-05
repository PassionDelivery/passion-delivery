package com.example.pdelivery.user.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.shared.security.JwtAccessDeniedHandler;
import com.example.pdelivery.shared.security.JwtAuthFilter;
import com.example.pdelivery.shared.security.JwtAuthenticationEntryPoint;
import com.example.pdelivery.shared.security.SecurityConfig;
import com.example.pdelivery.user.application.service.AuthService;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, AuthService.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class AuthControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	// DB boundary mock — real AuthService uses this
	@MockitoBean
	UserRepository userRepository;

	// SecurityConfig dependencies
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

	private Map<String, Object> validRequest() {
		Map<String, Object> req = new HashMap<>();
		req.put("username", "testuser");
		req.put("password", "Password1!");
		req.put("nickname", "nick");
		req.put("email", "a@b.com");
		req.put("role", "CUSTOMER");
		return req;
	}

	@Test
	void signup_success() throws Exception {
		// stub: username not taken, save returns the entity
		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validRequest())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.username").value("testuser"));
	}

	@Test
	void signup_validationFail() throws Exception {
		Map<String, Object> invalid = new HashMap<>(validRequest());
		invalid.put("username", "");

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalid)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	void signup_duplicateUsername() throws Exception {
		when(userRepository.existsByUsername(any())).thenReturn(true);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(validRequest())))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("AUTH_002"));
	}

	@Test
	void signup_managerRole() throws Exception {
		Map<String, Object> managerRequest = new HashMap<>(validRequest());
		managerRequest.put("role", "MANAGER");

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(managerRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("AUTH_001"));
	}
}
