package com.example.pdelivery.user.domain.entity;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

	@Column(nullable = false, unique = true, length = 100)
	private String username;

	@Column(nullable = false, unique = true, length = 20)
	private String nickname;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private UserRole role;

	public static UserEntity create(String username, String encodedPassword, String nickname, String email,
		UserRole role) {
		UserEntity user = new UserEntity();
		user.username = username;
		user.password = encodedPassword;
		user.nickname = nickname;
		user.email = email;
		user.role = role;
		return user;
	}

	public static UserEntity createMaster(String username, String encodedPassword, String nickname, String email) {
		return create(username, encodedPassword, nickname, email, UserRole.MASTER);
	}

}
