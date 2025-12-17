package com.example.api_backend.service.user;

import com.example.api_backend.dto.UserDto;
import com.example.api_backend.response.ListUserResponse;
import com.example.api_backend.request.UserRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {
    ListUserResponse getUsers(String keyword, Pageable pageable);
    UserDto getUser(String username);
    UserDto getUserById(Integer id);
    List<UserDto> getlist();
    void deleteUser(int id);
    UserDto createUser(UserRequest userRequest);
    UserDto updateUser(Integer id, UserRequest userRequest);

}
