package com.example.pdelivery.user.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.shared.security.JwtAccessDeniedHandler;
import com.example.pdelivery.shared.security.JwtAuthFilter;
import com.example.pdelivery.shared.security.JwtAuthenticationEntryPoint;
import com.example.pdelivery.shared.security.SecurityConfig;
import com.example.pdelivery.user.application.service.UserService;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, UserService.class})
@TestPropertySource(properties = {
	"app.cors.allowed-origins=http://localhost:3000",
	"app.jwt.secret=test-secret-key-that-is-at-least-32-characters-long-for-hs256",
	"app.jwt.expiration-ms=3600000",
	"spring.main.allow-bean-definition-overriding=true"
})
class UserControllerTest {

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

	@MockitoBean
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUpFilterPassThrough() throws ServletException, IOException {
		doAnswer(inv -> {
			inv.<FilterChain>getArgument(2).doFilter(inv.getArgument(0), inv.getArgument(1));
			return null;
		}).when(jwtAuthFilter).doFilter(any(), any(), any());
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void getUser_success() throws Exception {
		// given
		when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
			.thenReturn(Optional.of(
				UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER)));

		// when & then
		mockMvc.perform(get("/api/users/testuser"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value("testuser"));
	}

	@Test
	@WithMockUser(username = "otheruser", roles = "CUSTOMER")
	void getUser_forbidden() throws Exception {
		mockMvc.perform(get("/api/users/testuser"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("USER_002"));
	}

	@Test
	@WithMockUser(username = "nobody", roles = "CUSTOMER")
	void getUser_notFound() throws Exception {
		// given
		when(userRepository.findByUsernameAndDeletedAtIsNull("nobody"))
			.thenReturn(Optional.empty());

		// when & then
		mockMvc.perform(get("/api/users/nobody"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("USER_001"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void updateUser_success() throws Exception {
		// given
		when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
			.thenReturn(Optional.of(
				UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER)));
		when(userRepository.existsByNicknameAndUsernameNot("newNick", "testuser")).thenReturn(false);

		// when & then
		mockMvc.perform(patch("/api/users/testuser")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nickname\":\"newNick\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value("newNick"));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void updateUser_validationFail() throws Exception {
		// "weak" fails @Pattern (too short, missing uppercase/number/special)
		mockMvc.perform(patch("/api/users/testuser")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"password\":\"weak\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@WithMockUser(username = "testuser", roles = "CUSTOMER")
	void deleteUser_success() throws Exception {
		// given — entity needs a UUID id so softDelete(user.getId()) does not NPE
		UserEntity user = UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER);
		ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
		when(userRepository.findByUsernameAndDeletedAtIsNull("testuser"))
			.thenReturn(Optional.of(user));

		// when & then
		mockMvc.perform(delete("/api/users/testuser"))
			.andExpect(status().isNoContent());
	}

	@Test
	@WithMockUser(username = "otheruser", roles = "CUSTOMER")
	void deleteUser_forbidden() throws Exception {
		mockMvc.perform(delete("/api/users/testuser"))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("USER_002"));
	}
}
