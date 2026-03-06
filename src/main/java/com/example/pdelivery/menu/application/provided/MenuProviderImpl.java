package com.example.pdelivery.menu.application.provided;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.pdelivery.menu.domain.MenuRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MenuProviderImpl implements MenuProvider {

	private final MenuRepository menuRepository;

	@Override
	public Optional<MenuInfo> getMenu(UUID menuId) {

		// exception 안하고 empty() 반환
		return menuRepository.findById(menuId)
			.filter(entity -> !entity.isDeleted())
			.map(entity -> new MenuInfo(
				entity.getId(),
				entity.getMenu().getName(),
				entity.getMenu().getPrice(),
				entity.getMenu().getDescription(),
				entity.getMenu().getIsHidden()
			));
	}

	@Override
	public List<MenuInfo> getMenus(List<UUID> menuIds) {
		return menuRepository.findAllByIdIn(menuIds).stream()
			.filter(entity -> !entity.isDeleted())
			.map(entity -> new MenuInfo(
				entity.getId(),
				entity.getMenu().getName(),
				entity.getMenu().getPrice(),
				entity.getMenu().getDescription(),
				entity.getMenu().getIsHidden()
			))
			.toList();
	}
}
