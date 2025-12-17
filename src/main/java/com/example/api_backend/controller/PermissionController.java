package com.example.api_backend.controller;

import com.example.api_backend.dto.PermissionDto;
import com.example.api_backend.request.PermissionRequest;
import com.example.api_backend.response.ListPermissionResponse;
import com.example.api_backend.response.MessageResponse;
import com.example.api_backend.service.permission.IPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping("")
    public ResponseEntity<List<PermissionDto>> get(){
        return new ResponseEntity<>(permissionService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ListPermissionResponse> search(@RequestParam String keyword, @RequestParam int page, @RequestParam int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        ListPermissionResponse permissionResponse = permissionService.getSearch(keyword, pageRequest);
        return ResponseEntity.ok(permissionResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getById(@PathVariable Integer id){
        return new ResponseEntity<>(permissionService.getById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id){
        try {
            permissionService.delete(id);
            return new ResponseEntity<>(new MessageResponse("Permession delete successfully "+id), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<PermissionDto> create(@Valid @RequestBody PermissionRequest request){
        return new ResponseEntity<>(permissionService.save(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionDto> update(@PathVariable Integer id, @RequestBody PermissionRequest request){
        return new ResponseEntity<>(permissionService.update(id, request), HttpStatus.OK);
    }

}
