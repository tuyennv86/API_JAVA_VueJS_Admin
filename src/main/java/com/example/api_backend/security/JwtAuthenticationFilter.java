package com.example.api_backend.security;


import com.example.api_backend.repository.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService uds, TokenBlacklistRepository blk) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = uds;
        this.tokenBlacklistRepository = blk;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtils.validateToken(token)) {
                String jti = jwtUtils.getJtiFromToken(token);
                if (tokenBlacklistRepository.existsByJti(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                String username = jwtUtils.getUsernameFromToken(token);
                UserDetails ud = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
