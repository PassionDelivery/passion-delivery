package com.example.pdelivery.review.domain;

import java.util.UUID;

import com.example.pdelivery.shared.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_rating_stat")
public class RatingStatEntity extends BaseEntity {

	@Column(name = "store_id", nullable = false, unique = true)
	private UUID storeId;

	@Column(name = "avg_rating", nullable = false)
	private double avgRating;

	@Column(name = "review_cnt", nullable = false)
	private int reviewCnt;

	public static RatingStatEntity create(UUID storeId) {
		RatingStatEntity entity = new RatingStatEntity();
		entity.storeId = storeId;
		entity.avgRating = 0;
		entity.reviewCnt = 0;
		return entity;
	}

	/**
	 * 새로운 리뷰 생성 시: A += (X - A) / (N + 1)
	 */
	public void addRating(int rating) {
		this.avgRating += (rating - this.avgRating) / (this.reviewCnt + 1.0);
		this.reviewCnt++;
	}

	/**
	 * 리뷰 수정 시: A += (새별점 - 기존별점) / N
	 */
	public void updateRating(int oldRating, int newRating) {
		if (this.reviewCnt == 0) {
			return;
		}
		this.avgRating += (newRating - oldRating) / (double)this.reviewCnt;
	}

	/**
	 * 리뷰 삭제 시: N이 1 이하이면 초기화, 아니면 A += -(편차) / (N - 1)
	 */
	public void removeRating(int rating) {
		if (this.reviewCnt <= 1) {
			this.avgRating = 0;
			this.reviewCnt = 0;
			return;
		}
		this.avgRating += -(rating - this.avgRating) / (this.reviewCnt - 1.0);
		this.reviewCnt--;
	}
}
