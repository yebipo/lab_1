package com.antiprocrastinate.lab.service;

import com.antiprocrastinate.lab.exception.ResourceNotFoundException;
import com.antiprocrastinate.lab.model.User;
import com.antiprocrastinate.lab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public User findById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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