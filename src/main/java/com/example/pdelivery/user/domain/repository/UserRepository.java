package com.example.pdelivery.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

	boolean existsByNicknameAndIdNotAndDeletedAtIsNull(String nickname, UUID id);

	boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, UUID id);

	Page<UserEntity> findByUsernameContainingIgnoreCaseAndDeletedAtIsNull(String username, Pageable pageable);
}
