package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.mapper.UserMapper;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.service.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping
  public Set<UserDto> getAll() {
    return userService.findAll().stream()
        .map(userMapper::toDto)
        .collect(Collectors.toSet());
  }

  @PostMapping
  public UserDto create(@RequestBody User user) {
    return userMapper.toDto(userService.save(user));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }
}