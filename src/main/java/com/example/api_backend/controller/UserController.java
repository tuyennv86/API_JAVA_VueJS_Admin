package com.example.api_backend.controller;

import com.example.api_backend.dto.UserDto;
import com.example.api_backend.response.ListUserResponse;
import com.example.api_backend.request.UserRequest;
import com.example.api_backend.response.MessageResponse;
import com.example.api_backend.service.FileStorageService;
import com.example.api_backend.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/users")
@RequiredArgsConstructor

public class UserController {

    private final IUserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<List<UserDto>> list() {
        return new ResponseEntity<>(userService.getlist(), HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<UserDto> get(@PathVariable Integer id) {
       return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/getallusersearch")
    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<ListUserResponse>getAllUserSearch(
            @RequestParam String keyword, @RequestParam Integer page,
            @RequestParam Integer size, @RequestParam(defaultValue = "id") String sortBy ) {

        PageRequest pageRequest = PageRequest.of(page - 1,size, Sort.by(sortBy));
        ListUserResponse userResponse = userService.getUsers(keyword,pageRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete_users')")
    public ResponseEntity<MessageResponse> delete(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(new MessageResponse("User deleted successfully "+id), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("")
    //@PreAuthorize("hasAuthority('add_users')")
    public ResponseEntity<UserDto> createUser(
            @RequestPart("user") UserRequest userRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Nếu có ảnh thì upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Trả về ví dụ: "20251113/avatar.png"
            String relativePath = fileStorageService.storeFile(imageFile);
            userRequest.setImageUrl(relativePath);
        }

        UserDto userDto = userService.createUser(userRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    // 🔹 Cập nhật user
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('edit_users')")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Integer id,
            @RequestPart("user") UserRequest userRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Nếu có ảnh thì upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.storeFile(imageFile);
            userRequest.setImageUrl(fileName);
        }

        UserDto userDto = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(userDto);
    }

}
