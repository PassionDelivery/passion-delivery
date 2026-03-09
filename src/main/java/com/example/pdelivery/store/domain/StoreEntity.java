package com.example.pdelivery.store.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;
import com.example.pdelivery.store.error.StoreErrorCode;
import com.example.pdelivery.store.error.StoreException;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_store")
public class StoreEntity extends BaseEntity {

	@Column(name = "owner_id", nullable = false)
	private UUID ownerId;

	@Column(name = "category_id", nullable = false)
	private UUID categoryId;

	@Embedded
	private Store store;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 10)
	private StoreStatus status;

	@Column(name = "reject_code", length = 15)
	private String rejectCode;

	@Column(name = "reject_reason", length = 500)
	private String rejectReason;

	@Builder
	private StoreEntity(UUID ownerId, UUID categoryId, Store store, StoreStatus status) {
		this.ownerId = ownerId;
		this.categoryId = categoryId;
		this.store = store;
		this.status = status;
	}

	public static StoreEntity create(UUID ownerId, UUID categoryId, String name, String address, String phone,
		StoreStatus initialStatus) {
		Store store = new Store(name, address, phone);
		return StoreEntity.builder()
			.ownerId(ownerId)
			.categoryId(categoryId)
			.store(store)
			.status(initialStatus)
			.build();
	}

	public void approve() {
		if (this.status != StoreStatus.PENDING) {
			throw new StoreException(StoreErrorCode.INVALID_STATUS_CHANGE);
		}
		this.status = StoreStatus.APPROVED;
		this.rejectCode = null;
		this.rejectReason = null;
	}

	public void reject(String rejectCode, String rejectReason) {
		if (this.status != StoreStatus.PENDING) {
			throw new StoreException(StoreErrorCode.INVALID_STATUS_CHANGE);
		}
		this.status = StoreStatus.REJECTED;
		this.rejectCode = rejectCode;
		this.rejectReason = rejectReason;
	}

	public void updateInfo(String name, String address, String phone) {
		this.store = new Store(name, address, phone);
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}

	public boolean isOwnedBy(UUID userId) {
		return this.ownerId.equals(userId);
	}
}
