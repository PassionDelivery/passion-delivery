package com.example.pdelivery.user.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

import com.example.pdelivery.user.domain.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
	boolean existsByUsername(String username);

	Optional<UserEntity> findByUsername(String username);
}
