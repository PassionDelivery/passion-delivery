package com.example.pdelivery.shared;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends AbstractEntity {
	@CreatedDate
	private LocalDateTime createdAt;
	@CreatedBy
	private UUID createdBy;
	private LocalDateTime updatedAt;
	private UUID updatedBy;
	private LocalDateTime deletedAt;
	private UUID deletedBy;
}
