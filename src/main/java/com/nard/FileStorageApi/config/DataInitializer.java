package com.nard.FileStorageApi.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.nard.FileStorageApi.dao.AccountRepository;
import com.nard.FileStorageApi.dao.UserRepository;
import com.nard.FileStorageApi.model.Account;
import com.nard.FileStorageApi.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

  @Autowired
  private FileStorage fileStorage;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting application initialization...");

    // Check base directory
    checkBaseDirectory();

    // Initialize demo data
    initializeDemoData();

    log.info("Application initialization completed.");
  }

  private void checkBaseDirectory() {
    try {
      String basePath = fileStorage.getRootStorage();
      Path path = Paths.get(basePath);

      if (!Files.exists(path)) {
        log.info("Base directory does not exist. Creating: {}", basePath);
        Files.createDirectories(path);
      }

      if (!Files.isWritable(path)) {
        throw new RuntimeException("Base directory is not writable: " + basePath);
      }

      log.info("Base directory verified: {}", basePath);
    } catch (Exception e) {
      log.error("Error checking base directory", e);
      throw new RuntimeException("Failed to initialize base directory", e);
    }
  }

  private void initializeDemoData() {
    // Create demo account if it doesn't exist
    Account demoAccount = accountRepository.findAll().stream()
        .filter(acc -> "Demo Account".equals(acc.getName()))
        .findFirst()
        .orElse(null);

    if (demoAccount == null) {
      demoAccount = new Account();
      demoAccount.setName("Demo Account");
      demoAccount.setEmail("demo@example.com");
      demoAccount.setCreatedAt(LocalDateTime.now());
      demoAccount = accountRepository.save(demoAccount);
      log.info("Created demo account: {}", demoAccount.getName());
    }

    // Create demo user if it doesn't exist
    User demoUser = userRepository.findByName("demo-app").orElse(null);

    if (demoUser == null) {
      // Generate a demo API key (plaintext for demo purposes - in production,
      // generate securely)
      String plainApiKey = "demo-api-key-12345";
      String hashedApiKey = passwordEncoder.encode(plainApiKey);

      demoUser = new User();
      demoUser.setName("demo-app");
      demoUser.setStatus("ACTIVE");
      demoUser.setAccount(demoAccount);
      demoUser.setCreatedAt(LocalDateTime.now());
      demoUser.setApiKey(hashedApiKey);
      demoUser = userRepository.save(demoUser);

      log.info("Created demo user: {}", demoUser.getName());
      log.info("Demo API Key (plaintext - for testing): {}", plainApiKey);
      log.info("IMPORTANT: Save this API key for testing. It will not be shown again.");
    } else {
      log.info("Demo user already exists: {}", demoUser.getName());
    }
  }

}
