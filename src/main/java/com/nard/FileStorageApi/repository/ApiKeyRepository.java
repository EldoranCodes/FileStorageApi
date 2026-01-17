package com.nard.FileStorageApi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nard.FileStorageApi.model.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

  Optional<ApiKey> findById(Long id);

  Optional<ApiKey> findByApiKey(String key);

  boolean existsByApiKey(String key);

}
