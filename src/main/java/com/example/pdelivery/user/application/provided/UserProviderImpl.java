package com.example.pdelivery.user.application.provided;

import java.util.UUID;

import com.example.pdelivery.shared.Provider;
import com.example.pdelivery.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Provider
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

	private final UserRepository userRepository;

	@Override
	public boolean existsById(UUID userId) {
		return userRepository.findByIdAndDeletedAtIsNull(userId).isPresent();
	}
}
