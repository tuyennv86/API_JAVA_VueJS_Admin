package com.example.api_backend.controller;

import com.example.api_backend.dto.RoleDto;
import com.example.api_backend.request.RoleRequest;
import com.example.api_backend.response.ListRoleResponse;
import com.example.api_backend.response.MessageResponse;
import com.example.api_backend.service.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("${app.api.prefix}/roles")
@RequiredArgsConstructor

public class RoleController {
    private final IRoleService roleService;

//    @PreAuthorize("hasAuthority('view_users')")
    @GetMapping("")
    public ResponseEntity<List<RoleDto>> list() {
        return new ResponseEntity<>(roleService.getlist(), HttpStatus.OK);
    }
    @GetMapping("/search")
//    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<ListRoleResponse>getAllUserSearch(
            @RequestParam String keyword, @RequestParam Integer page, @RequestParam Integer size ) {

        PageRequest pageRequest = PageRequest.of(page - 1,size);
        ListRoleResponse roleResponse = roleService.search(keyword,pageRequest);
        return new ResponseEntity<>(roleResponse, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(roleService.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        try {
            roleService.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Role delete successfully "+ id),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()),HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("")
    public ResponseEntity<RoleDto> create(@Valid @RequestBody RoleRequest roleRequest) {
        RoleDto roleDto = roleService.save(roleRequest);
        return new ResponseEntity<>(roleDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> update(@PathVariable Integer id, @RequestBody RoleRequest roleRequest) {
        RoleDto roleDto = roleService.update(id, roleRequest);
        return new ResponseEntity<>(roleDto, HttpStatus.OK);
    }
}
