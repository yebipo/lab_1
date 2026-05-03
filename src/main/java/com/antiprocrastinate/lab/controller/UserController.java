package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.PageResponse;
import com.antiprocrastinate.lab.dto.UserCreateDto;
import com.antiprocrastinate.lab.dto.UserResponseDto;
import com.antiprocrastinate.lab.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
  public UserResponseDto getMe(Principal principal) {
    return userService.findByUsername(principal.getName());
  }

  @PutMapping("/me")
  public UserResponseDto updateMe(Principal principal, @Valid @RequestBody UserCreateDto dto) {
    return userService.updateByUsername(principal.getName(), dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public PageResponse<UserResponseDto> getAll(Pageable pageable) {
    return PageResponse.of(userService.findAll(pageable));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public UserResponseDto getById(@PathVariable Long id) {
    return userService.findById(id);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
    return userService.create(dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public UserResponseDto update(@PathVariable Long id, @Valid @RequestBody UserCreateDto dto) {
    return userService.update(id, dto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }
}