package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Cacheable(value = "users", key = "{#pageable.pageNumber, #pageable.pageSize}")
  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Cacheable(value = "users", key = "#id")
  @Transactional(readOnly = true)
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public List<User> saveAll(List<User> users) {
    return userRepository.saveAll(users);
  }

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public List<User> patchBulk(List<UserDto> dtos) {
    List<Long> ids = dtos.stream().map(UserDto::getId).toList();
    List<User> existingUsers = userRepository.findAllById(ids);

    if (existingUsers.size() != ids.size()) {
      throw new ResourceNotFoundException("Один или несколько пользователей не найдены");
    }

    Map<Long, UserDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(
            UserDto::getId, dto -> dto, (existing, replacement) -> replacement));

    existingUsers.forEach(existing -> {
      UserDto dto = dtoMap.get(existing.getId());
      if (dto.getUsername() != null) {
        existing.setUsername(dto.getUsername());
      }
      if (dto.getEmail() != null) {
        existing.setEmail(dto.getEmail());
      }
      if (dto.getLevel() != null) {
        existing.setLevel(dto.getLevel());
      }
      if (dto.getDailyGoalMinutes() != null) {
        existing.setDailyGoalMinutes(dto.getDailyGoalMinutes());
      }
      if (dto.getAvatarUrl() != null) {
        existing.setAvatarUrl(dto.getAvatarUrl());
      }
    });

    return userRepository.saveAll(existingUsers);
  }

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    userRepository.deleteAllByIdInBatch(ids);
  }
}