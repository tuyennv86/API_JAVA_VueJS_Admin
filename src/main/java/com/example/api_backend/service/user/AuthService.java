package com.example.api_backend.service.user;

import com.example.api_backend.request.AuthRequest.LoginRequest;
import com.example.api_backend.request.AuthRequest.RegisterRequest;
import com.example.api_backend.response.LoginResponse;
import com.example.api_backend.model.*;
import com.example.api_backend.repository.RefreshTokenRepository;
import com.example.api_backend.repository.RoleRepository;
import com.example.api_backend.repository.TokenBlacklistRepository;
import com.example.api_backend.repository.UserRepository;
import com.example.api_backend.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${app.jwt.refreshTokenExpirationMs}")
    private long refreshExpirationMs;

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) throw new RuntimeException("username exists");
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        roleRepository.findByName("user").ifPresent(r -> u.getRoles().add(r));
        userRepository.save(u);
    }

    public LoginResponse login(LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            //Tạo JWT token
            User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
            String accessToken = jwtUtils.generateAccessToken(user);

            //Tạo vào lưu RefreshToken
            String refreshTokenStr = UUID.randomUUID().toString() + RandomStringUtils.randomAlphanumeric(64);
            RefreshToken rt = new RefreshToken();
            rt.setToken(refreshTokenStr);
            rt.setUser(user);
            rt.setCreatedAt(LocalDateTime.now());
            rt.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
            refreshTokenRepository.save(rt);

        // Lấy roles & permissions
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        List<String> perms = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getImageUrl(),
                roles,
                perms
        );
        return new LoginResponse(userInfo, accessToken, refreshTokenStr);
        }catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }

    public LoginResponse refreshToken(String refreshTokenStr) {
        // 1️⃣ Tìm refresh token trong DB
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2️⃣ Kiểm tra hạn sử dụng
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired, please login again");
        }

        User user = refreshToken.getUser();

        // 3️⃣ Sinh access token mới
        String newAccessToken = jwtUtils.generateAccessToken(user);

        // 4️⃣ (Tùy chọn) Sinh refresh token mới mỗi lần
        String newRefreshTokenStr = UUID.randomUUID().toString() + RandomStringUtils.randomAlphanumeric(64);

        refreshToken.setToken(newRefreshTokenStr);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        refreshTokenRepository.save(refreshToken);

        // 5️⃣ Lấy roles & permissions
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        List<String> perms = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        // 6️⃣ Trả về JSON như login
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getImageUrl(),
                roles,
                perms
        );

        return new LoginResponse(userInfo, newAccessToken, newRefreshTokenStr);
    }


    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null && jwtUtils.validateToken(accessToken)) {
            String jti = jwtUtils.getJtiFromToken(accessToken);
            TokenBlacklist bl = new TokenBlacklist();
            bl.setJti(jti);
            bl.setToken(accessToken);
            bl.setExpiryDate(jwtUtils.getExpirationDate(accessToken).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            tokenBlacklistRepository.save(bl);
        }
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
        }
    }
}
