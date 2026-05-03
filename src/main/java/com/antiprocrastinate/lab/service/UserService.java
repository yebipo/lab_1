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
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setLevel(1); // Дефолтное значение
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

    userMapper.updateEntity(dto, existing);
    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      existing.setPassword(passwordEncoder.encode(dto.getPassword()));
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