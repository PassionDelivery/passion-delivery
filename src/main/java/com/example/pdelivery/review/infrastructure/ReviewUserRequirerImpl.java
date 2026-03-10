package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import com.example.pdelivery.shared.annotations.Requirer;
import com.example.pdelivery.user.application.provided.UserProvider;

import lombok.RequiredArgsConstructor;

@Requirer
@RequiredArgsConstructor
public class ReviewUserRequirerImpl implements ReviewUserRequirer {

	private final UserProvider userProvider;

	@Override
	public boolean existsBy(UUID customerId) {
		return userProvider.existsById(customerId);
	}
}
