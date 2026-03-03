package com.example.pdelivery.shared;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
public class JpaAuditConfig {
	@Bean
	public AuditorAware<UUID> auditorProvider() {
		return new AuditorAwareImpl();
	}
}
