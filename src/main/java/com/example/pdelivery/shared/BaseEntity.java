package com.example.pdelivery.shared;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
	@LastModifiedDate
	private LocalDateTime updatedAt;
	@LastModifiedBy
	private UUID updatedBy;
	private LocalDateTime deletedAt;
	private UUID deletedBy;

	public void softDelete(UUID deletedByUserId) {
		if (this.deletedAt != null) {
			return;
		}
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = Objects.requireNonNull(deletedByUserId, "deletedByUserId must not be null");
	}
}
