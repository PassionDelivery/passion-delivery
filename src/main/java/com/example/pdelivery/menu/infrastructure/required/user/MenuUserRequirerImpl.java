package com.example.pdelivery.menu.infrastructure.required.user;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MenuUserRequirerImpl implements MenuUserRequirer {

	// private final UserProvider userProvider;
	private final UserData userData = new UserData(UUID.randomUUID());

	@Override
	public UserData getUserByUsername(String username) {
		// UserData userData = userProvider.getUserByUsername(username);
		if (userData == null) {
			throw new MenuException(MenuErrorCode.USER_NOT_FOUND);
		}
		return userData;
		/*
			TODO: User 모듈 구현 후 UserProvider에 위임하도록 교체
		 */
	}
}
