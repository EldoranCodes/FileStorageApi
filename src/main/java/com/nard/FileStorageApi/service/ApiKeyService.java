package com.nard.FileStorageApi.service;

import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.model.ApiKey;
import com.nard.FileStorageApi.model.Consumer;
import com.nard.FileStorageApi.repository.ApiKeyRepository;
import com.nard.FileStorageApi.repository.ConsumerRepository;

@Service
public class ApiKeyService {

  private ConsumerRepository consumerRepo;
  private ApiKeyRepository apkiRepo;

  public ApiKeyService(ConsumerRepository consumerRepo, ApiKeyRepository apkiRepo) {
    this.consumerRepo = consumerRepo;
    this.apkiRepo = apkiRepo;
  }

  // if valid it will return the consumers data
  public ApiKey validateApiKey(String apiKeyInput) {
    Optional<ApiKey> k = apkiRepo.findByApiKey(apiKeyInput);
    if (k.isEmpty()) {
      return null;
    }
    ApiKey validApiKey = k.get();

    Optional<Consumer> c = consumerRepo.findById(validApiKey.getOwner());
    if (c.isEmpty())
      return null;

    return validApiKey;
  }

}
