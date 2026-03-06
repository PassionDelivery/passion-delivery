package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

import com.example.pdelivery.shared.Requirer;

@Requirer
public class ReviewUserRequirerImpl implements ReviewUserRequirer {
	@Override
	public boolean existsBy(UUID customerId) {
		return true;
	}
}
