package com.example.pdelivery.user.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pdelivery.shared.security.AuthUser;
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

	private Authentication customerAuth(UUID userId, String username) {
		return new UsernamePasswordAuthenticationToken(
			new AuthUser(userId, username, UserRole.CUSTOMER), null,
			List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
		);
	}

	private UserEntity userEntityWithId(UUID id) throws Exception {
		UserEntity user = UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER);
		Field idField = com.example.pdelivery.shared.AbstractEntity.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(user, id);
		return user;
	}

	@Test
	void getMe_success() throws Exception {
		UUID userId = UUID.randomUUID();
		when(userRepository.findByIdAndDeletedAtIsNull(userId))
			.thenReturn(Optional.of(UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER)));

		mockMvc.perform(get("/api/users/me")
				.with(authentication(customerAuth(userId, "testuser"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value("testuser"));
	}

	@Test
	void getMe_notFound() throws Exception {
		UUID userId = UUID.randomUUID();
		when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/users/me")
				.with(authentication(customerAuth(userId, "testuser"))))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("USER_001"));
	}

	@Test
	void updateMe_success() throws Exception {
		UUID userId = UUID.randomUUID();
		when(userRepository.findByIdAndDeletedAtIsNull(userId))
			.thenReturn(Optional.of(UserEntity.create("testuser", "hash", "nick", "a@b.com", UserRole.CUSTOMER)));
		when(userRepository.existsByNicknameAndIdNotAndDeletedAtIsNull("newNick", userId)).thenReturn(false);

		mockMvc.perform(patch("/api/users/me")
				.with(authentication(customerAuth(userId, "testuser")))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nickname\":\"newNick\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.nickname").value("newNick"));
	}

	@Test
	void updateMe_validationFail() throws Exception {
		UUID userId = UUID.randomUUID();

		mockMvc.perform(patch("/api/users/me")
				.with(authentication(customerAuth(userId, "testuser")))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"password\":\"weak\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void deleteMe_success() throws Exception {
		UUID userId = UUID.randomUUID();
		when(userRepository.findByIdAndDeletedAtIsNull(userId))
			.thenReturn(Optional.of(userEntityWithId(userId)));

		mockMvc.perform(delete("/api/users/me")
				.with(authentication(customerAuth(userId, "testuser"))))
			.andExpect(status().isNoContent());
	}

}
