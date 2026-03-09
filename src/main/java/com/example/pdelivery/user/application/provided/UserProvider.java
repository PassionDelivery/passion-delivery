package com.example.pdelivery.user.application.provided;

import java.util.UUID;

public interface UserProvider {

	boolean existsById(UUID userId);
}
