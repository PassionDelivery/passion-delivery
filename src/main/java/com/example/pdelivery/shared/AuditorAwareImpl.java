package com.example.pdelivery.shared;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.pdelivery.user.domain.repository.UserRepository;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

	private final UserRepository userRepository;

	public AuditorAwareImpl(@Lazy UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Optional<UUID> getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return Optional.empty();
		}
		return userRepository.findByUsername(auth.getName())
			.map(user -> user.getId());
	}
}
