package com.example.api_backend.security;

import com.example.api_backend.config.CorsProperties;
import com.example.api_backend.config.SecurityPathsConfig;
import com.example.api_backend.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final SecurityPathsConfig securityPathsConfig;
    private final CorsProperties corsProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtils, userDetailsService, tokenBlacklistRepository);

    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Cấu hình CORS toàn cục
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> {
                // ✅ Cho phép các route public từ YAML
                securityPathsConfig.getPermitAllPaths()
                        .forEach(path -> auth.requestMatchers(path).permitAll());
                auth.anyRequest().authenticated();
            });

    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}

    // ✅ Đọc CORS config từ YAML
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(corsProperties.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
