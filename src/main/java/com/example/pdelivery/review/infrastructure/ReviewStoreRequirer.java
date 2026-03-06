package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

public interface ReviewStoreRequirer {
	boolean existsBy(UUID storeId);
}
