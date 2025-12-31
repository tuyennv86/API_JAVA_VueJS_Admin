package com.example.api_backend.controller;

import com.example.api_backend.dto.MenuDto;
import com.example.api_backend.model.Menu;
import com.example.api_backend.model.Role;
import com.example.api_backend.model.User;
import com.example.api_backend.repository.MenuRepository;
import com.example.api_backend.repository.UserRepository;
import com.example.api_backend.request.MenuRequest;
import com.example.api_backend.response.MessageResponse;
import com.example.api_backend.security.JwtUtils;
import com.example.api_backend.service.menu.IMenuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.prefix}/menus")

public class MenuController {

    private final JwtUtils jwtUtils;
    private final IMenuService menuService;
    private final MenuRepository menuRepository;

    @GetMapping("")
    public ResponseEntity< List<MenuDto>> list() {
        return new ResponseEntity<>(menuService.getAllMenus(), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MenuDto>> myMenus(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.ok(Collections.emptyList());
        String username = authentication.getName();
        List<MenuDto> list = menuService.myMenus(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getbyuser")
//    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<List<MenuDto>> getByUser(HttpServletRequest request) {
        try {
            String token = jwtUtils.extractTokenFromRequest(request);
            String username = jwtUtils.getUsernameFromToken(token);

            List<MenuDto> menus = menuService.getMenusByUser(username);
            return ResponseEntity.ok(menus);
        }catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuDto>> search(@RequestParam String keyword) {
        List<MenuDto> list = menuService.getByKeyword(keyword);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/others/{id}")
    public ResponseEntity<List<MenuDto>> getOthers(@PathVariable Integer id) {
        List<MenuDto> list;
        if(id == null || id <= 0) {
            list = menuService.getAllMenus();
        }else {
            list = menuService.getOthers(id);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> get(@PathVariable Integer id) {
        return new ResponseEntity<>(menuService.getMenuById(id), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<MenuDto> create(@RequestBody MenuRequest menuRequest) {
        return new ResponseEntity<>(menuService.create(menuRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDto> update(@PathVariable Integer id, @RequestBody MenuRequest menuRequest) {
        return new ResponseEntity<>(menuService.update(id,menuRequest),HttpStatus.OK);
    }

    @PutMapping("changIsActive/{id}")
    public ResponseEntity<MenuDto> changIsActive(@PathVariable Integer id) {
        return new ResponseEntity<>(menuService.changisActive(id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        try {
            menuService.delete(id);
            return new ResponseEntity<>(new MessageResponse("Menu deleted successfully "+id), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
