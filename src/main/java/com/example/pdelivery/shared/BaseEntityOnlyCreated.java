package com.example.pdelivery.shared;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntityOnlyCreated extends AbstractEntity {
	private LocalDateTime createdAt;
	private UUID createdBy;
}
