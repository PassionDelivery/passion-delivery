package com.example.pdelivery.user.presentation.dto;

import com.example.pdelivery.user.domain.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

	@NotBlank
	@Pattern(regexp = "[a-z0-9]{4,10}", message = "username은 4~10자의 알파벳 소문자, 숫자만 가능합니다.")
	private String username;

	@NotBlank
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$",
		message = "password는 8~15자, 대소문자·숫자·특수문자를 모두 포함해야 합니다."
	)
	private String password;

	@NotBlank
	private String nickname;

	@NotBlank
	@Email
	private String email;

	@NotNull
	private UserRole role;
}
