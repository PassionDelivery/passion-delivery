package com.example.pdelivery.user.application.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.pdelivery.shared.AuditorAwareImpl;
import com.example.pdelivery.shared.JpaAuditConfig;
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

	// Helper — creates and persists a test user
	private UserEntity savedUser(String username) {
		return userRepository.save(
			UserEntity.create(username, passwordEncoder.encode("Password1!"), "nick_" + username,
				username + "@test.com", UserRole.CUSTOMER)
		);
	}

	@Test
	void getUser_success() {
		// given
		savedUser("testuser");

		// when
		var result = userService.getUser("testuser");

		// then
		assertThat(result.username()).isEqualTo("testuser");
		assertThat(result.createdAt()).isNotNull();
	}

	@Test
	void getUser_notFound() {
		assertThatThrownBy(() -> userService.getUser("nobody"))
			.isInstanceOf(UserException.class)
			.satisfies(e -> assertThat(((UserException)e).getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND));
	}

	@Test
	void updateUser_success() {
		// given
		savedUser("testuser");

		// when
		var result = userService.updateUser("testuser",
			new UpdateUserRequestDto("newNick", null, null));

		// then
		assertThat(result.nickname()).isEqualTo("newNick");
	}

	@Test
	void updateUser_duplicateNickname() {
		// given: user2 has nickname "nick_user2"
		savedUser("user1");
		savedUser("user2");

		// when: user1 tries to take user2's nickname
		assertThatThrownBy(() ->
			userService.updateUser("user1", new UpdateUserRequestDto("nick_user2", null, null)))
			.isInstanceOf(UserException.class)
			.satisfies(e -> assertThat(((UserException)e).getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME));
	}

	@Test
	void updateUser_allFieldsNull() {
		// given
		savedUser("testuser");

		// when: all fields null — must be rejected at service layer
		assertThatThrownBy(() ->
			userService.updateUser("testuser", new UpdateUserRequestDto(null, null, null)))
			.isInstanceOf(UserException.class);
	}

	@Test
	void deleteUser_success() {
		// given
		savedUser("testuser");

		// when
		userService.deleteUser("testuser");

		// then: soft-deleted user is not returned by findByUsernameAndDeletedAtIsNull
		Optional<UserEntity> found = userRepository.findByUsernameAndDeletedAtIsNull("testuser");
		assertThat(found).isEmpty();
	}

}
