package com.example.pdelivery.review.infrastructure;

import java.util.List;
import java.util.UUID;

public interface ReviewStoreRequirer {
	boolean existsBy(UUID storeId);

	List<UUID> findStoreIdsByOwnerId(UUID ownerId);
}
