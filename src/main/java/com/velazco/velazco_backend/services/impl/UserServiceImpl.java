package com.velazco.velazco_backend.services.impl;

import com.velazco.velazco_backend.repositories.UserRepository;
import com.velazco.velazco_backend.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}