package com.nard.FileStorageApi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.repository.ClientsRepository;

@TestConfiguration
public class TestDataConfig {

  @Bean
  public Client testClient(ClientsRepository repo) {

    Client existing = repo.findByClientId(2L);
    if (existing != null) {
      return existing;
    }

    Client c = new Client();
    c.setClientName("todoApp");
    c.setApiKey("test-api-key");
    return repo.save(c);
  }
}
