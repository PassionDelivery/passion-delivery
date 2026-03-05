package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

public interface ReviewUserRequirer {
	boolean existsBy(UUID customerId);
}
