package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.config.security.JwtUtils;
import com.antiprocrastinate.lab.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authManager;
  private final JwtUtils jwtUtils;

  @PostMapping("/login")
  public String login(@RequestBody LoginRequest loginRequest) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));
    return jwtUtils.generateToken(loginRequest.getUsername());
  }
}