package com.example.pdelivery.menu.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pdelivery.menu.domain.Menu;
import com.example.pdelivery.menu.domain.MenuEntity;
import com.example.pdelivery.menu.domain.MenuRepository;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;

	@Override
	public MenuResponse createMenu(UUID storeId, MenuCreateRequest request) {

		// Menu VO 생성
		// 추후 MenuEntity(FK), AI 기능 할 예정
		Menu menu = new Menu(
			request.name(),
			request.price(),
			request.description()
		);

		// Entity 생성
		MenuEntity menuEntity = new MenuEntity(storeId, menu);

		// 저장
		MenuEntity saved = menuRepository.save(menuEntity);

		// Response 변환
		return MenuResponse.from(saved);
	}
}