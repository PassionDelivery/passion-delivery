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
import java.util.Optional;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.shared.security.JwtAccessDeniedHandler;
import com.example.pdelivery.shared.security.JwtAuthFilter;
import com.example.pdelivery.shared.security.JwtAuthenticationEntryPoint;
import com.example.pdelivery.shared.security.JwtUtil;
import com.example.pdelivery.shared.security.SecurityConfig;
import com.example.pdelivery.user.application.service.LoginService;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthTokenController.class)
@Import({SecurityConfig.class, LoginService.class, JwtUtil.class})
@TestPropertySource(properties = {
		"app.cors.allowed-origins=http://localhost:3000",
		"app.jwt.secret=test-secret-key-that-is-at-least-32-characters-long-for-hs256",
		"app.jwt.expiration-ms=3600000"
})
class AuthTokenControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	UserRepository userRepository;

	@MockitoBean
	JwtAuthFilter jwtAuthFilter;

	@MockitoBean
	JwtAuthenticationEntryPoint authEntryPoint;

	@MockitoBean
	JwtAccessDeniedHandler accessDeniedHandler;

	@BeforeEach
	void setUpFilterPassThrough() throws ServletException, IOException {
		doAnswer(inv -> {
			ServletRequest req = inv.getArgument(0);
			ServletResponse res = inv.getArgument(1);
			FilterChain chain = inv.getArgument(2);
			chain.doFilter(req, res);
			return null;
		}).when(jwtAuthFilter).doFilter(any(), any(), any());
	}

	private Map<String, Object> loginRequest(String username, String password) {
		Map<String, Object> req = new HashMap<>();
		req.put("username", username);
		req.put("password", password);
		return req;
	}

	@Test
	void login_success() throws Exception {
		// BCrypt-encode the expected password so real BCryptPasswordEncoder matches it
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode("Password1!");

		when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
				.thenReturn(Optional.of(UserEntity.create("testuser", hash, "nick", "a@b.com", UserRole.CUSTOMER)));

		mockMvc.perform(post("/api/auth/tokens")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest("testuser", "Password1!"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.accessToken").isNotEmpty());
	}

	@Test
	void login_userNotFound() throws Exception {
		when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

		mockMvc.perform(post("/api/auth/tokens")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest("unknown", "Password1!"))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTH_003"));
	}

	@Test
	void login_wrongPassword() throws Exception {
		// BCrypt-encode a DIFFERENT password — matches() will return false for "Password1!"
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashForDifferentPassword = encoder.encode("OtherPassword9@");

		when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
				.thenReturn(Optional.of(
						UserEntity.create("testuser", hashForDifferentPassword, "nick", "a@b.com", UserRole.CUSTOMER)));

		mockMvc.perform(post("/api/auth/tokens")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest("testuser", "Password1!"))))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.code").value("AUTH_003"));
	}
}
