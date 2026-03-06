package com.example.pdelivery.menu.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class MenuEntityTest {

	@Autowired
	MenuRepository menuRepository;

	@Autowired
	EntityManager em;

	@Test
	@DisplayName("메뉴를 DB에 저장하고 조회한다")
	void saveAndFind() {
		UUID storeId = UUID.randomUUID();
		MenuEntity menu = MenuEntity.create(storeId, "치킨", 20000, "바삭한 치킨", null);

		MenuEntity saved = menuRepository.save(menu);

		em.flush();   // INSERT SQL 강제 실행
		em.clear();   // 1차 캐시 초기화 → findById가 실제 SELECT 실행

		MenuEntity found = menuRepository.findById(saved.getId())
			.orElseThrow();

		assertThat(found.getStoreId()).isEqualTo(storeId);
		assertThat(found.getMenu().getName()).isEqualTo("치킨");
		assertThat(found.getMenu().getPrice()).isEqualTo(20000);
		assertThat(found.getMenu().getDescription()).isEqualTo("바삭한 치킨");
		assertThat(found.getMenu().getIsHidden()).isFalse();
		assertThat(found.getCreatedAt()).isNotNull();
	}
}
