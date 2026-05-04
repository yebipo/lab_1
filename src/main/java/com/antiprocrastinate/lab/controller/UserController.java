package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.config.security.CustomUserDetails;
import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.UserCreateDto;
import com.antiprocrastinate.lab.dto.UserResponseDto;
import com.antiprocrastinate.lab.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public UserResponseDto getMe(@AuthenticationPrincipal CustomUserDetails user) {
    return userService.findById(user.getId());
  }

  @PutMapping("/me")
  public UserResponseDto updateMe(
      @AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody UserCreateDto dto) {
    return userService.update(user.getId(), dto);
  }

  @GetMapping
  public PageResponse<UserResponseDto> getAll(Pageable pageable) {
    return PageResponse.of(userService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public UserResponseDto getById(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
    return userService.create(dto);
  }

  @PutMapping("/{id}")
  public UserResponseDto update(@PathVariable Long id, @Valid @RequestBody UserCreateDto dto) {
    return userService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }
}