package com.nard.FileStorageApi.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nard.FileStorageApi.dao.UserRepository;
import com.nard.FileStorageApi.model.User;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ApiKeyService apiKeyService;

  private BCryptPasswordEncoder passwordEncoder;
  private User activeUser;
  private User inactiveUser;
  private String validApiKey;
  private String hashedApiKey;

  @BeforeEach
  void setUp() {
    passwordEncoder = new BCryptPasswordEncoder();
    validApiKey = "test-api-key-123";
    hashedApiKey = passwordEncoder.encode(validApiKey);

    activeUser = new User();
    activeUser.setId(1L);
    activeUser.setName("test-user");
    activeUser.setStatus("ACTIVE");
    activeUser.setApiKey(hashedApiKey);

    inactiveUser = new User();
    inactiveUser.setId(2L);
    inactiveUser.setName("inactive-user");
    inactiveUser.setStatus("INACTIVE");
    inactiveUser.setApiKey(hashedApiKey);
  }

  @Test
  void testValidateApiKey_ValidKey_ReturnsUser() {
    when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(activeUser));

    Optional<User> result = apiKeyService.validateApiKey(validApiKey);

    assertTrue(result.isPresent());
    assertTrue(result.get().getId().equals(1L));
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testValidateApiKey_InvalidKey_ReturnsEmpty() {
    when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(activeUser));

    Optional<User> result = apiKeyService.validateApiKey("invalid-key");

    assertFalse(result.isPresent());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testValidateApiKey_InactiveUser_ReturnsEmpty() {
    when(userRepository.findAll()).thenReturn(java.util.Arrays.asList(inactiveUser));

    Optional<User> result = apiKeyService.validateApiKey(validApiKey);

    assertFalse(result.isPresent());
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void testValidateApiKey_NullKey_ReturnsEmpty() {
    Optional<User> result = apiKeyService.validateApiKey(null);

    assertFalse(result.isPresent());
    verify(userRepository, never()).findAll();
  }

  @Test
  void testValidateApiKey_EmptyKey_ReturnsEmpty() {
    Optional<User> result = apiKeyService.validateApiKey("");

    assertFalse(result.isPresent());
    verify(userRepository, never()).findAll();
  }

}
