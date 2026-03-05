package com.example.pdelivery.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.example.pdelivery.shared.security.JwtUtil;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.AuthErrorCode;
import com.example.pdelivery.user.error.AuthException;
import com.example.pdelivery.user.presentation.dto.LoginRequestDto;
import com.example.pdelivery.user.presentation.dto.LoginResponseDto;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@DataJpaTest
@Import(JwtUtil.class)
@TestPropertySource(properties = {
		"app.jwt.secret=test-secret-key-that-is-at-least-32-characters-long-for-hs256",
		"app.jwt.expiration-ms=3600000"
})
class LoginServiceTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtil jwtUtil;

	PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

	LoginService loginService;

	private static final String TEST_SECRET = "test-secret-key-that-is-at-least-32-characters-long-for-hs256";

	@BeforeEach
	void setUp() {
		loginService = new LoginService(userRepository, passwordEncoder, jwtUtil);
	}

	@Test
	void login_success() {
		// given
		UserEntity user = UserEntity.create("testuser", "Password1!", "nick", "a@b.com", UserRole.CUSTOMER);
		userRepository.save(user);

		LoginRequestDto dto = new LoginRequestDto("testuser", "Password1!");

		// when
		LoginResponseDto result = loginService.login(dto);

		// then
		assertThat(result.accessToken()).isNotNull();
		assertThat(jwtUtil.extractUsername(result.accessToken())).isEqualTo("testuser");

		// decode claims directly to verify role
		SecretKey signingKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
		String role = Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(result.accessToken())
				.getPayload()
				.get("role", String.class);
		assertThat(role).isEqualTo("CUSTOMER");
	}

	@Test
	void login_userNotFound() {
		// given
		LoginRequestDto dto = new LoginRequestDto("nonexistent", "Password1!");

		// when & then
		assertThatThrownBy(() -> loginService.login(dto))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode())
						.isEqualTo(AuthErrorCode.INVALID_CREDENTIALS));
	}

	@Test
	void login_wrongPassword() {
		// given
		UserEntity user = UserEntity.create("testuser", "Password1!", "nick", "a@b.com", UserRole.CUSTOMER);
		userRepository.save(user);

		LoginRequestDto dto = new LoginRequestDto("testuser", "WrongPassword!");

		// when & then
		assertThatThrownBy(() -> loginService.login(dto))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode())
						.isEqualTo(AuthErrorCode.INVALID_CREDENTIALS));
	}

	@Test
	void login_deletedUser() {
		// given
		UserEntity user = UserEntity.create("testuser", "Password1!", "nick", "a@b.com", UserRole.CUSTOMER);
		userRepository.save(user);

		// soft-delete the user via reflection on BaseEntity.deletedAt
		try {
			Field deletedAtField = user.getClass().getSuperclass().getDeclaredField("deletedAt");
			deletedAtField.setAccessible(true);
			deletedAtField.set(user, LocalDateTime.now());
			userRepository.save(user);
		} catch (Exception e) {
			throw new RuntimeException("Failed to set deletedAt via reflection", e);
		}

		LoginRequestDto dto = new LoginRequestDto("testuser", "Password1!");

		// when & then
		assertThatThrownBy(() -> loginService.login(dto))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode())
						.isEqualTo(AuthErrorCode.INVALID_CREDENTIALS));
	}
}
