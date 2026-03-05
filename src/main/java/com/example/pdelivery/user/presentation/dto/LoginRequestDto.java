package com.example.pdelivery.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

	@NotBlank
	private String username;

	@NotBlank
	private String password;
}
