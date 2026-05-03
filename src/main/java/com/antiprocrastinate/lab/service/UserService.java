package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.mapper.UserMapper;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Cacheable(value = "user_item", key = "#id")
  @Transactional(readOnly = true)
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  @CachePut(value = "user_item", key = "#result.id")
  @Transactional
  public User create(UserDto dto) {
    User user = userMapper.toEntity(dto);
    return userRepository.save(user);
  }

  @CachePut(value = "user_item", key = "#id")
  @Transactional
  public User update(Long id, UserDto dto) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    userMapper.updateEntityFromDto(dto, existing);
    return userRepository.save(existing);
  }

  @CacheEvict(value = "user_item", allEntries = true)
  @Transactional
  public List<User> patchBulk(List<UserDto> dtos) {
    List<Long> ids = dtos.stream().map(UserDto::getId).toList();
    List<User> existingUsers = userRepository.findAllById(ids);

    if (existingUsers.size() != ids.size()) {
      throw new ResourceNotFoundException("Один или несколько пользователей не найдены");
    }

    Map<Long, UserDto> dtoMap = dtos.stream()
        .collect(Collectors.toMap(UserDto::getId, dto -> dto));

    existingUsers.forEach(existing -> {
      UserDto dto = dtoMap.get(existing.getId());
      userMapper.updateEntityFromDto(dto, existing);
    });

    return userRepository.saveAll(existingUsers);
  }

  @CacheEvict(value = "user_item", key = "#id")
  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  @CacheEvict(value = "user_item", allEntries = true)
  @Transactional
  public void deleteAll(List<Long> ids) {
    userRepository.deleteAllByIdInBatch(ids);
  }
}