package com.example.pdelivery.user.domain.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.repository.Repository;

import com.example.pdelivery.user.domain.entity.UserEntity;

public interface UserRepository extends Repository<UserEntity, UUID> {

	UserEntity save(UserEntity entity);

	boolean existsByUsername(String username);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByUsernameAndDeletedAtIsNull(String username);

	boolean existsByUsernameAndDeletedAtIsNull(String username);

	boolean existsByNicknameAndUsernameNot(String nickname, String username);

	boolean existsByEmailAndUsernameNot(String email, String username);
}
