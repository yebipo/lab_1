package com.antiprocrastinate.lab.repository;

import com.antiprocrastinate.lab.model.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@NullMarked
public interface UserRepository extends JpaRepository<User, Long> {
}