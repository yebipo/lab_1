package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.dto.UserDto;
import com.antiprocrastinate.lab.mapper.UserMapper;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserMapper userMapper;
  @InjectMocks private UserService userService;

  private User testUser;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    pageable = PageRequest.of(0, 10);
  }

  @Test
  void shouldFindAll() {
    Page<User> page = new PageImpl<>(List.of(testUser));
    when(userRepository.findAll(pageable)).thenReturn(page);
    assertThat(userService.findAll(pageable).getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    assertThat(userService.findById(1L).getId()).isEqualTo(1L);
  }

  @Test
  void shouldCreate() {
    UserDto dto = new UserDto();
    when(userMapper.toEntity(dto)).thenReturn(testUser);
    when(userRepository.save(testUser)).thenReturn(testUser);

    User result = userService.create(dto);
    assertThat(result).isNotNull();
    verify(userRepository).save(testUser);
  }

  @Test
  void shouldUpdate() {
    UserDto dto = new UserDto();
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(testUser)).thenReturn(testUser);

    userService.update(1L, dto);
    verify(userMapper).updateEntityFromDto(dto, testUser);
    verify(userRepository).save(testUser);
  }

  @Test
  void shouldPatchBulk() {
    UserDto dto = new UserDto(); dto.setId(1L);
    when(userRepository.findAllById(List.of(1L))).thenReturn(List.of(testUser));
    when(userRepository.saveAll(anyList())).thenReturn(List.of(testUser));

    userService.patchBulk(List.of(dto));
    verify(userMapper).updateEntityFromDto(dto, testUser);
    verify(userRepository).saveAll(anyList());
  }

  @Test
  void shouldDeleteById() {
    userService.deleteById(1L);
    verify(userRepository).deleteById(1L);
  }

  @Test
  void shouldDeleteAllInBatch() {
    List<Long> ids = List.of(1L, 2L);
    userService.deleteAll(ids);
    verify(userRepository).deleteAllByIdInBatch(ids);
  }
}