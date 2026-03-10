package com.example.pdelivery.user.application.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.pdelivery.shared.jpa.AuditorAwareImpl;
import com.example.pdelivery.shared.jpa.JpaAuditConfig;
import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;
import com.example.pdelivery.user.error.UserErrorCode;
import com.example.pdelivery.user.error.UserException;
import com.example.pdelivery.user.presentation.dto.UpdateUserRequestDto;

@DataJpaTest
@Import({JpaAuditConfig.class, AuditorAwareImpl.class})
class UserServiceTest {

	@Autowired
	UserRepository userRepository;

	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, passwordEncoder);
	}

	// Helper — creates and persists a test user, returns entity with assigned UUID
	private UserEntity savedUser(String username) {
		return userRepository.save(
			UserEntity.create(username, passwordEncoder.encode("Password1!"), "nick_" + username,
				username + "@test.com", UserRole.CUSTOMER)
		);
	}

	@Test
	void getUser_success() {
		UUID userId = savedUser("testuser").getId();

		var result = userService.getUser(userId);

		assertThat(result.username()).isEqualTo("testuser");
		assertThat(result.createdAt()).isNotNull();
	}

	@Test
	void getUser_notFound() {
		assertThatThrownBy(() -> userService.getUser(UUID.randomUUID()))
			.isInstanceOf(UserException.class)
			.satisfies(e -> assertThat(((UserException)e).getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND));
	}

	@Test
	void updateUser_success() {
		UUID userId = savedUser("testuser").getId();

		var result = userService.updateUser(userId, new UpdateUserRequestDto("newNick", null, null));

		assertThat(result.nickname()).isEqualTo("newNick");
	}

	@Test
	void updateUser_duplicateNickname() {
		UUID user1Id = savedUser("user1").getId();
		savedUser("user2"); // nick_user2

		assertThatThrownBy(() ->
			userService.updateUser(user1Id, new UpdateUserRequestDto("nick_user2", null, null)))
			.isInstanceOf(UserException.class)
			.satisfies(e -> assertThat(((UserException)e).getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME));
	}

	@Test
	void updateUser_allFieldsNull() {
		UUID userId = savedUser("testuser").getId();

		assertThatThrownBy(() ->
			userService.updateUser(userId, new UpdateUserRequestDto(null, null, null)))
			.isInstanceOf(UserException.class);
	}

	@Test
	void deleteUser_success() {
		UserEntity user = savedUser("testuser");
		UUID userId = user.getId();

		userService.deleteUser(userId, userId);

		Optional<UserEntity> found = userRepository.findByUsernameAndDeletedAtIsNull("testuser");
		assertThat(found).isEmpty();
	}

}
