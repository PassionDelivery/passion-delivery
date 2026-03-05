package com.example.pdelivery.menu.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdelivery.menu.application.MenuService;
import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/{storeId}/menus")
public class MenuController {

	private final MenuService menuService;

	@PostMapping
	public MenuResponse createMenu(
		@PathVariable UUID storeId,
		@RequestBody MenuCreateRequest request
	) {
		return menuService.createMenu(storeId, request);
	}

}