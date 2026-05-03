package com.antiprocrastinate.lab.controller;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.mapper.UserMapper;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @GetMapping
  @Operation(summary = "Получить всех пользователей (с пагинацией)")
  public Page<UserDto> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return userService.findAll(PageRequest.of(page, size)).map(userMapper::toDto);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Получить пользователя по ID")
  public UserDto getById(@PathVariable Long id) {
    return userMapper.toDto(userService.findById(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Создать нового пользователя")
  public UserDto create(@Valid @RequestBody UserDto userDto) {
    User user = userMapper.toEntity(userDto);
    return userMapper.toDto(userService.save(user));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Обновить пользователя")
  public UserDto update(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
    User user = userMapper.toEntity(userDto);
    user.setId(id);
    return userMapper.toDto(userService.save(user));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Удалить пользователя")
  public void delete(@PathVariable Long id) {
    userService.deleteById(id);
  }

  @PostMapping("/bulk")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Массовое создание пользователей")
  public List<UserDto> createBulk(@Valid @RequestBody List<UserDto> dtos) {
    List<User> users = dtos.stream().map(userMapper::toEntity).toList();
    return userService.saveAll(users).stream().map(userMapper::toDto).toList();
  }

  @PutMapping("/bulk")
  @Operation(summary = "Массовое обновление пользователей (полное)")
  public List<UserDto> updateBulk(@Valid @RequestBody List<UserDto> dtos) {
    List<User> users = dtos.stream().map(userMapper::toEntity).toList();
    return userService.saveAll(users).stream().map(userMapper::toDto).toList();
  }

  @PatchMapping("/bulk")
  @Operation(summary = "Массовое обновление пользователей (частичное)")
  public List<UserDto> patchBulk(@RequestBody List<UserDto> dtos) {
    return userService.patchBulk(dtos).stream().map(userMapper::toDto).toList();
  }

  @DeleteMapping("/bulk")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Массовое удаление пользователей")
  public void deleteBulk(@RequestBody List<Long> ids) {
    userService.deleteAll(ids);
  }
}