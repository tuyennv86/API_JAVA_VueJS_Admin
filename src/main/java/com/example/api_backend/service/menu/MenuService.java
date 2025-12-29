package com.example.api_backend.service.menu;

import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.mapper.MenuMapper;
import com.example.api_backend.mapper.PermissionMapper;
import com.example.api_backend.model.Menu;
import com.example.api_backend.model.Permission;
import com.example.api_backend.model.Role;
import com.example.api_backend.model.User;
import com.example.api_backend.repository.MenuRepository;
import com.example.api_backend.repository.PermissionRepository;
import com.example.api_backend.repository.UserRepository;
import com.example.api_backend.request.MenuRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class MenuService implements IMenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final MenuMapper menuMapper;
    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public List<MenuDto> getAllMenus() {
        List<Menu> list = menuRepository.findAll();
        List<Menu> treeMenus = buildMenuTree(list);
        return menuMapper.toDtoList(treeMenus);
    }

    @Override
    public List<MenuDto> getMenusByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found: " + username));
        Set<Role> roles = user.getRoles();
        Set<Integer> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
        List<Menu> flatMenus = menuRepository.findByRoleIds(roleIds);
        List<Menu> treeMenus = buildMenuTree(flatMenus);
        return menuMapper.toDtoList(treeMenus);
    }

    @Override
    public List<MenuDto> myMenus(String username) {
        User u = userRepository.findByUsername(username).orElseThrow();
        Set<Role> roles = u.getRoles();
        List<Menu> list = menuRepository.findByRoleIds(roles.stream().map(Role::getId).collect(Collectors.toSet()));
        return menuMapper.toDtoList(buildMenuTree(list));
    }

    @Override
    public MenuDto getMenuById(int id) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy menu có id =: " + id));
        Hibernate.initialize(menu.getPermissions());
        MenuDto menuDto = menuMapper.toDto(menu);
        menuDto.setPermissions(menu.getPermissions());
        return menuDto;
    }

    @Override
    public void delete(Integer id) {
        // kiểm tra xem id có tồn tại trong db hay không
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu không tồn tai id ="+id));
        // xóa các liên kết
        menu.getPermissions().clear();
        //menu.getRoles().clear();
        menuRepository.save(menu);
        //xóa menu
        menuRepository.deleteById(id);
    }

    @Override
    public MenuDto update(Integer id, MenuRequest menuRequest) {
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu not found"));
        menu.setName(menuRequest.getName());
        menu.setPath(menuRequest.getPath());
        menu.setIcon(menuRequest.getIcon());
        menu.setParentId(menuRequest.getParentId());
        menu.setSortOrder(menuRequest.getSortOrder());
        menu.setIsActive(menuRequest.getIsActive());

        //clear menu_permission
        menu.getPermissions().clear();
        if(menuRequest.getPermissionIds() != null && !menuRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(menuRequest.getPermissionIds()));
            menu.getPermissions().addAll(permissions);
        }

        menuRepository.save(menu);
        return menuMapper.toDto(menu);
    }

    @Override
    public MenuDto create(MenuRequest menuRequest) {

        Menu menu = new Menu();
        menu.setName(menuRequest.getName());
        menu.setPath(menuRequest.getPath());
        menu.setIcon(menuRequest.getIcon());
        menu.setParentId(menuRequest.getParentId());
        menu.setSortOrder(menuRequest.getSortOrder());
        menu.setIsActive(menuRequest.getIsActive());

        if(menuRequest.getPermissionIds() != null && !menuRequest.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(menuRequest.getPermissionIds()));
            menu.getPermissions().addAll(permissions);
        }

        menuRepository.save(menu);
        return menuMapper.toDto(menu);
    }

    @Override
    public List<MenuDto> getByKeyword(String keyword) {
       List<Menu> menus = menuRepository.findByKeyword(keyword);
       List<Menu> treeMenus = buildMenuTree(menus);
       return menuMapper.toDtoList(treeMenus);
    }

    @Override
    public List<MenuDto> getOthers(Integer id) {
        List<Menu> menus = menuRepository.findMenusOher(id);
        List<Menu> treeMenus = buildMenuTree(menus);
        return menuMapper.toDtoList(treeMenus);
    }

    // ✅ Xây cây cha–con nếu có cha mà không tìm thấy cha thì đua lên đâu
    private List<Menu> buildMenuTree(List<Menu> menus) {
        Map<Integer, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, m -> m));
        List<Menu> rootMenus = new ArrayList<>();
        for (Menu menu : menus) {
            Integer parentId = menu.getParentId();
            if (parentId == null) {
                rootMenus.add(menu);
            } else {
                Menu parent = menuMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                } else {
                    // parent không tồn tại → đưa lên root
                    rootMenus.add(menu);
                }
            }
        }
        sortRecursively(rootMenus);
        return rootMenus;
    }

    private void sortRecursively(List<Menu> menus) {
        menus.sort(Comparator.comparing(Menu::getSortOrder));

        for (Menu menu : menus) {
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                sortRecursively(menu.getChildren());
            }
        }
    }
}
