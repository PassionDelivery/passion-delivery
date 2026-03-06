package com.example.pdelivery.menu.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.pdelivery.menu.error.MenuErrorCode;
import com.example.pdelivery.menu.error.MenuException;

class MenuTest {

	@Nested
	@DisplayName("메뉴 생성 성공")
	class CreateSuccess {

		@Test
		@DisplayName("필수 필드만으로 메뉴를 생성한다")
		void createWithRequiredFields() {
			Menu menu = new Menu("치킨", 20000, null);

			assertThat(menu.getName()).isEqualTo("치킨");
			assertThat(menu.getPrice()).isEqualTo(20000);
			assertThat(menu.getDescription()).isNull();
			assertThat(menu.getIsHidden()).isFalse();
		}

		@Test
		@DisplayName("모든 필드로 메뉴를 생성한다")
		void createWithAllFields() {
			Menu menu = new Menu("치킨", 20000, "바삭한 치킨", true);

			assertThat(menu.getName()).isEqualTo("치킨");
			assertThat(menu.getPrice()).isEqualTo(20000);
			assertThat(menu.getDescription()).isEqualTo("바삭한 치킨");
			assertThat(menu.getIsHidden()).isTrue();
		}

		@Test
		@DisplayName("가격이 0원인 메뉴를 생성한다")
		void createWithZeroPrice() {
			Menu menu = new Menu("물", 0, null);

			assertThat(menu.getPrice()).isZero();
		}

		@Test
		@DisplayName("isHidden이 null이면 false로 기본 설정된다")
		void createWithNullIsHidden() {
			Menu menu = new Menu("치킨", 20000, null, null);

			assertThat(menu.getIsHidden()).isFalse();
		}
	}

	@Nested
	@DisplayName("메뉴 생성 실패")
	class CreateFail {

		@Test
		@DisplayName("이름이 null이면 실패한다")
		void failWithNullName() {
			assertThatThrownBy(() -> new Menu(null, 20000, null))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_NAME));
		}

		@Test
		@DisplayName("이름이 빈 문자열이면 실패한다")
		void failWithBlankName() {
			assertThatThrownBy(() -> new Menu("  ", 20000, null))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_NAME));
		}

		@Test
		@DisplayName("이름이 100자를 초과하면 실패한다")
		void failWithTooLongName() {
			String longName = "가".repeat(101);
			assertThatThrownBy(() -> new Menu(longName, 20000, null))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_NAME));
		}

		@Test
		@DisplayName("가격이 null이면 실패한다")
		void failWithNullPrice() {
			assertThatThrownBy(() -> new Menu("치킨", null, null))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_PRICE));
		}

		@Test
		@DisplayName("가격이 음수이면 실패한다")
		void failWithNegativePrice() {
			assertThatThrownBy(() -> new Menu("치킨", -1, null))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_PRICE));
		}

		@Test
		@DisplayName("설명이 500자를 초과하면 실패한다")
		void failWithTooLongDescription() {
			String longDesc = "가".repeat(501);
			assertThatThrownBy(() -> new Menu("치킨", 20000, longDesc))
				.isInstanceOf(MenuException.class)
				.satisfies(e -> assertThat(((MenuException) e).getErrorCode())
					.isEqualTo(MenuErrorCode.INVALID_MENU_DESCRIPTION));
		}
	}
}
