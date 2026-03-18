package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public Set<User> findAll() {
    return new HashSet<>(userRepository.findAll());
  }

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional
  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }
}