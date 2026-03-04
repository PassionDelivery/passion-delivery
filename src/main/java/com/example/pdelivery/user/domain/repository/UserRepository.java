package com.example.pdelivery.user.domain.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.example.pdelivery.user.domain.entity.UserEntity;

public interface UserRepository extends Repository<UserEntity, UUID> {

	UserEntity save(UserEntity entity);

	boolean existsByUsername(String username);

	Optional<UserEntity> findByUsername(String username);
}
