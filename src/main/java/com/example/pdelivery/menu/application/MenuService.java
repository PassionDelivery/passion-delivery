package com.example.pdelivery.menu.application;

import java.util.UUID;

import com.example.pdelivery.menu.presentation.dto.MenuCreateRequest;
import com.example.pdelivery.menu.presentation.dto.MenuResponse;

public interface MenuService {

	MenuResponse createMenu(UUID storeId, MenuCreateRequest request);
}