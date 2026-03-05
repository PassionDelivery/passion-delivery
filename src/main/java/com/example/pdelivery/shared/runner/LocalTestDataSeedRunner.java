package com.example.pdelivery.shared.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.entity.UserRole;
import com.example.pdelivery.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class LocalTestDataSeedRunner implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private static final String LOCAL_PASSWORD = "Local1234!";

	@Override
	public void run(ApplicationArguments args) {
		seedUser("local_customer", "로컬고객",   "local_customer@pdelivery.com", UserRole.CUSTOMER);
		seedUser("local_owner",    "로컬사장",   "local_owner@pdelivery.com",    UserRole.OWNER);
		seedUser("local_manager",  "로컬관리자", "local_manager@pdelivery.com",  UserRole.MANAGER);
	}

	private void seedUser(String username, String nickname, String email, UserRole role) {
		if (userRepository.existsByUsername(username)) {
			return;
		}
		try {
			userRepository.save(
				UserEntity.create(username, passwordEncoder.encode(LOCAL_PASSWORD), nickname, email, role)
			);
			log.info("Local test seed user created: {} ({})", username, role);
		} catch (DataIntegrityViolationException ex) {
			log.info("Local test seed user already exists: {}", username);
		}
	}
}
