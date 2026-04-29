package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
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

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

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
    Page<User> result = userService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldFindById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    User result = userService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowWhenNotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void shouldSave() {
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    userService.save(testUser);
    verify(userRepository).save(testUser);
  }

  @Test
  void shouldDeleteById() {
    userService.deleteById(1L);
    verify(userRepository).deleteById(1L);
  }

  @Test
  void shouldSaveAll() {
    List<User> users = List.of(testUser);
    when(userRepository.saveAll(users)).thenReturn(users);

    List<User> result = userService.saveAll(users);
    assertThat(result).hasSize(1);
    verify(userRepository).saveAll(users);
  }

  @Test
  void shouldDeleteAll() {
    List<Long> ids = List.of(1L, 2L);
    userService.deleteAll(ids);
    verify(userRepository).deleteAllByIdInBatch(ids);
  }
}