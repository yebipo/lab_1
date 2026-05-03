package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.config.security.CustomUserDetails;
import com.antiprocrastinate.lab.config.security.JwtUtils;
import com.antiprocrastinate.lab.dto.LoginRequest;
import com.antiprocrastinate.lab.dto.UserCreateDto;
import com.antiprocrastinate.lab.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authManager;
  private final JwtUtils jwtUtils;
  private final UserService userService;

  public String login(LoginRequest loginRequest) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));

    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    return jwtUtils.generateToken(userDetails);
  }

  public UserResponseDto register(UserCreateDto dto) {
    return userService.create(dto);
  }
}