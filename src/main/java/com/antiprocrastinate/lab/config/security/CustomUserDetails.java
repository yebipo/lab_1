package com.antiprocrastinate.lab.config.security;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
}