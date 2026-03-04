package com.example.pdelivery.shared.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;

import com.example.pdelivery.user.domain.entity.UserEntity;
import com.example.pdelivery.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterSeedRunner implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.master.username}")
	private String masterUsername;

	@Value("${app.master.password}")
	private String masterPassword;

	@Value("${app.master.nickname}")
	private String masterNickname;

	@Value("${app.master.email}")
	private String masterEmail;

	@Override
	public void run(ApplicationArguments args) {
		if (userRepository.existsByUsername(masterUsername)) {
			return;
		}
		try {
			UserEntity master = UserEntity.createMaster(
				masterUsername,
				passwordEncoder.encode(masterPassword),
				masterNickname,
				masterEmail
			);
			userRepository.save(master);
			log.info("MASTER seed account created: {}", masterUsername);
		} catch (DataIntegrityViolationException ex) {
			log.info("MASTER seed account already exists: {}", masterUsername);
		}
	}
}
