package com.example.api_backend.service.menu;

import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.request.MenuRequest;

import java.util.List;

public interface IMenuService {
    List<MenuDto> getAllMenus();
    List<MenuDto> getMenusByUser(String username);
    List<MenuDto> myMenus(String username);
    MenuDto getMenuById(int id);
    void delete(Integer id);
    MenuDto update(Integer id, MenuRequest menuRequest);
    MenuDto create(MenuRequest menuRequest);
    List<MenuDto> getByKeyword(String keyword);
    List<MenuDto> getOthers(Integer id);
}
