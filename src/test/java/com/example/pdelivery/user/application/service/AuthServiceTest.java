package com.example.pdelivery.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.AuthErrorCode;
import com.example.pdelivery.user.error.AuthException;
import com.example.pdelivery.user.presentation.dto.SignupRequestDto;
import com.example.pdelivery.user.presentation.dto.SignupResponseDto;

@DataJpaTest
class AuthServiceTest {

	@Autowired
	UserRepository userRepository;

	PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();

	AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder);
	}

	@Test
	void signup_success() {
		// given
		SignupRequestDto dto = createDto("user1", "Password1!", "nick1", "a@b.com", UserRole.CUSTOMER);

		// when
		SignupResponseDto result = authService.signup(dto);

		// then
		assertThat(result.username()).isEqualTo("user1");
		assertThat(userRepository.existsByUsername("user1")).isTrue();
	}

	@Test
	void signup_duplicateUsername() {
		// given: save first user
		SignupRequestDto first = createDto("user1", "Password1!", "nick1", "a@b.com", UserRole.CUSTOMER);
		authService.signup(first);

		SignupRequestDto duplicate = createDto("user1", "Password2@", "nick2", "b@c.com", UserRole.CUSTOMER);

		// when & then
		assertThatThrownBy(() -> authService.signup(duplicate))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode()).isEqualTo(AuthErrorCode.DUPLICATE_USERNAME));
	}

	@Test
	void signup_managerRole() {
		// given
		SignupRequestDto dto = createDto("mgr1", "Password1!", "mgrNick", "mgr@b.com", UserRole.MANAGER);

		// when & then
		assertThatThrownBy(() -> authService.signup(dto))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode()).isEqualTo(AuthErrorCode.INVALID_ROLE));
	}

	@Test
	void signup_masterRole() {
		// given
		SignupRequestDto dto = createDto("mstr1", "Password1!", "mstrNick", "mstr@b.com", UserRole.MASTER);

		// when & then
		assertThatThrownBy(() -> authService.signup(dto))
				.isInstanceOf(AuthException.class)
				.satisfies(e -> assertThat(((AuthException) e).getErrorCode()).isEqualTo(AuthErrorCode.INVALID_ROLE));
	}

	// Helper — SignupRequestDto has @NoArgsConstructor + @Getter (Lombok), no all-args constructor.
	// We must use reflection-friendly approach via a test-only subclass or field-setting.
	// Since the DTO is package-accessible with Lombok @NoArgsConstructor, we use a test builder.
	private SignupRequestDto createDto(String username, String password, String nickname, String email, UserRole role) {
		try {
			SignupRequestDto dto = new SignupRequestDto();
			setField(dto, "username", username);
			setField(dto, "password", password);
			setField(dto, "nickname", nickname);
			setField(dto, "email", email);
			setField(dto, "role", role);
			return dto;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create SignupRequestDto", e);
		}
	}

	private void setField(Object obj, String fieldName, Object value) throws Exception {
		java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(obj, value);
	}
}
