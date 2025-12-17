package com.example.api_backend.service.user;
import com.example.api_backend.config.FileStorageProperties;
import com.example.api_backend.dto.UserDto;
import com.example.api_backend.mapper.UserMapper;
import com.example.api_backend.model.Role;
import com.example.api_backend.model.User;
import com.example.api_backend.repository.RoleRepository;
import com.example.api_backend.repository.UserRepository;
import com.example.api_backend.response.ListUserResponse;
import com.example.api_backend.request.UserRequest;
import com.example.api_backend.service.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor

public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FileStorageService fileStorageService;

    @Override
    public ListUserResponse getUsers(String keyword, Pageable pageable) {
        Page<User> users = userRepository.findUserSearch(keyword, pageable);
        return ListUserResponse.builder()
                .list(userMapper.toDtoList(users.getContent()))
                .total(users.getTotalPages())
                .totalItems((int)users.getTotalElements()).build();
    }

    @Override
    public UserDto getUser(String username) {
        return userMapper.toDto(userRepository.findByUsername(username).orElseThrow());
    }

    @Override
    public UserDto getUserById(Integer id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Override
    public List<UserDto> getlist() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        // check user tồn tại hay ko
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User không tồn tại"));
        // xoa  liên kết role trước
        user.getRoles().clear();
        userRepository.save(user);
        // xóa ảnh
        String oldFilePath = user.getImageUrl();
        if (oldFilePath != null && !oldFilePath.isBlank()) {
            fileStorageService.deleteFile(oldFilePath);
        }
        // xóa user
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserRequest userRequest) {
        // kiểm tra email có tồn tại hay không
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DataIntegrityViolationException(userRequest.getEmail() + ": Email này đã tồn tại");
        }
        // kiểm tra xem username tồn tại không
        if(userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DataIntegrityViolationException(userRequest.getUsername() + ": UserName này đã tồn tại");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        user.setIsActive(userRequest.getIsActive());
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        // Ảnh upload — chỉ cần set đường dẫn tương đối (VD: "20251113/avatar.jpg")
        if (userRequest.getImageUrl() != null && !userRequest.getImageUrl().isBlank()) {
            user.setImageUrl(userRequest.getImageUrl());
        }
        // lấy danh sách các role
        if(userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userRequest.getRoleIds()));
            user.setRoles(roles);
        }
        userRepository.save(user);
        return userMapper.toDto(user);

    }

    @Override
    @Transactional
    public UserDto updateUser(Integer id, UserRequest userRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy user có id ="+ id));
        // Kiểm tra email trùng, nhưng bỏ qua user hiện tại
        Optional<User> existingEmailUser = userRepository.findUserByEmail(userRequest.getEmail());
        if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(id)) {
            throw new DataIntegrityViolationException("Email "+userRequest.getEmail()+" đã tồn tại");
        }

        // kiểm tra username trùng, nhưng bỏ qua user hiện tại
        Optional<User> existingUserNameUser = userRepository.findByUsername(userRequest.getUsername());
        if(existingUserNameUser.isPresent() && !existingUserNameUser.get().getId().equals(id)) {
            throw new DataIntegrityViolationException("UserName "+userRequest.getUsername()+" đã tồn tại");
        }

        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        user.setIsActive(userRequest.getIsActive());
        if(userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));
        }
        // Nếu có ảnh mới, xóa ảnh cũ và lưu ảnh mới
        if (userRequest.getImageUrl() != null) {
            String oldFilePath = user.getImageUrl();
            if (oldFilePath != null && !oldFilePath.isBlank()) {
                fileStorageService.deleteFile(oldFilePath);
            }
            user.setImageUrl(userRequest.getImageUrl()); // đường dẫn tương đối như "20251113/avatar.jpg"
        }

        // cập nhật roles
        user.getRoles().clear();// xoa list role cũ để cập nhật role mới
        if(userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(userRequest.getRoleIds()));
            user.getRoles().addAll(roles);
        }
        userRepository.save(user);
        return userMapper.toDto(user);
    }

}
