package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.LoginRequest;
import com.antiprocrastinate.lab.dto.UserCreateDto;
import com.antiprocrastinate.lab.dto.UserResponseDto;
import com.antiprocrastinate.lab.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest loginRequest) {
    return authService.login(loginRequest);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto register(@Valid @RequestBody UserCreateDto dto) {
    return authService.register(dto);
  }
}