package com.example.pdelivery.review.infrastructure;

import java.util.UUID;

public interface ReviewOrderRequirer {
	OrderData getOrderInfo(UUID uuid);
}
