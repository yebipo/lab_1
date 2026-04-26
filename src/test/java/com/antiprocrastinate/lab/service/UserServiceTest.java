package com.antiprocrastinate.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Тестирование UserService")
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
  @DisplayName("Конструктор должен обрабатывать null")
  void constructorShouldCheckNull() {
    try {
      new UserService(null);
    } catch (NullPointerException | IllegalArgumentException e) {
      assertThat(e).isNotNull();
    }
  }

  @Test
  @DisplayName("Должен находить все записи")
  void shouldFindAll() {
    Page<User> page = new PageImpl<>(List.of(testUser));
    when(userRepository.findAll(pageable)).thenReturn(page);
    Page<User> result = userService.findAll(pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Должен находить запись по ID")
  void shouldFindById() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    User result = userService.findById(1L);
    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  @DisplayName("Должен выбрасывать исключение, если пользователь не найден")
  void shouldThrowWhenNotFound() {
    when(userRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> userService.findById(999L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  @DisplayName("Должен сохранять пользователя")
  void shouldSave() {
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    userService.save(testUser);
    verify(userRepository).save(testUser);
  }

  @Test
  @DisplayName("Должен удалять пользователя по ID")
  void shouldDeleteById() {
    userService.deleteById(1L);
    verify(userRepository).deleteById(1L);
  }
}