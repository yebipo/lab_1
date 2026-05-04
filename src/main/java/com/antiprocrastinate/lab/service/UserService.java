package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.UserCreateDto;
import com.antiprocrastinate.lab.dto.UserResponseDto;
import com.antiprocrastinate.lab.exception.BusinessOperationException;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.UserMapper;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  private static final String USER_NOT_FOUND_MSG = "User not found: ";

  @Transactional(readOnly = true)
  public Page<UserResponseDto> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(userMapper::toResponseDto);
  }

  @Transactional(readOnly = true)
  public UserResponseDto findById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));
  }

  @Transactional
  public UserResponseDto create(UserCreateDto dto) {
    checkUniqueness(dto.getUsername(), dto.getEmail(), null);

    User user = userMapper.toEntity(dto);
    // Используем newPassword для создания
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    user.setLevel(1);
    return userMapper.toResponseDto(userRepository.save(user));
  }

  @Transactional
  public UserResponseDto update(Long id, UserCreateDto dto) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MSG + id));
    return updateInternal(existing, dto);
  }

  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  private UserResponseDto updateInternal(User existing, UserCreateDto dto) {
    checkUniqueness(dto.getUsername(), dto.getEmail(), existing);

    // Запоминаем текущий пароль перед обновлением сущности маппером
    String currentHashedPassword = existing.getPassword();

    userMapper.updateEntity(dto, existing);

    // Логика смены пароля
    if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
      // Если меняем пароль, старый обязателен
      if (dto.getOldPassword() == null || dto.getOldPassword().isBlank()) {
        throw new BusinessOperationException("Для смены пароля необходимо ввести старый пароль");
      }

      // Проверяем соответствие старого пароля
      if (!passwordEncoder.matches(dto.getOldPassword(), currentHashedPassword)) {
        throw new BusinessOperationException("Неверный старый пароль");
      }

      existing.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    } else {
      // Если пароль не меняется, гарантируем сохранение старого хэша
      existing.setPassword(currentHashedPassword);
    }

    return userMapper.toResponseDto(userRepository.save(existing));
  }

  private void checkUniqueness(String username, String email, User existing) {
    if ((existing == null || !existing.getUsername().equals(username))
        && userRepository.existsByUsername(username)) {
      throw new BusinessOperationException("Пользователь с таким именем уже существует");
    }
    if ((existing == null || !existing.getEmail().equals(email))
        && userRepository.existsByEmail(email)) {
      throw new BusinessOperationException("Пользователь с таким email уже существует");
    }
  }
}