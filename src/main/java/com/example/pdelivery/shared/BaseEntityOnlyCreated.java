package com.example.pdelivery.shared;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntityOnlyCreated extends AbstractEntity {
	@CreatedDate
	private LocalDateTime createdAt;
	@CreatedBy
	private UUID createdBy;
}
