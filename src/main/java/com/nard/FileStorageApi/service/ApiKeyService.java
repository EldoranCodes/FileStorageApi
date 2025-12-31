package com.nard.FileStorageApi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.dao.UserRepository;
import com.nard.FileStorageApi.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiKeyService {

  @Autowired
  private UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Validates API key and returns the associated user
   * 
   * @param apiKey Plain text API key from request
   * @return User if API key is valid, empty otherwise
   */
  public Optional<User> validateApiKey(String apiKey) {
    if (apiKey == null || apiKey.trim().isEmpty()) {
      return Optional.empty();
    }

    // Get all users and check if any API key matches
    return userRepository.findAll().stream()
        .filter(user -> user.getStatus() != null && "ACTIVE".equals(user.getStatus()))
        .filter(user -> user.getApiKey() != null && passwordEncoder.matches(apiKey, user.getApiKey()))
        .findFirst();
  }

}
