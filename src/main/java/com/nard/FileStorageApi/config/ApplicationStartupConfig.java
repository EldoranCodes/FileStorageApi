package com.nard.FileStorageApi.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.nard.FileStorageApi.model.ApiKey;
import com.nard.FileStorageApi.model.Consumer;
import com.nard.FileStorageApi.repository.ApiKeyRepository;
import com.nard.FileStorageApi.repository.ConsumerRepository;

@Configuration
public class ApplicationStartupConfig implements CommandLineRunner {

  private final Logger log = LoggerFactory.getLogger(ApplicationStartupConfig.class);

  private FileStorageProperty fileStorageProperty;

  private ConsumerRepository consumerRepo;
  private ApiKeyRepository apiKeyRepo;

  public ApplicationStartupConfig(FileStorageProperty fileStorageProperty, ConsumerRepository consumerRepository,
      ApiKeyRepository apiKeyrepo) {
    this.apiKeyRepo = apiKeyrepo;
    this.consumerRepo = consumerRepository;
    this.fileStorageProperty = fileStorageProperty;
  }

  @Override
  public void run(String... args) throws Exception {

    String basePath = ensureBasePath();
    log.info(basePath);

    // seed a consumer
    Consumer c = new Consumer("nard", "active", "admin");
    Consumer savedC = consumerRepo.save(c);

    log.info(savedC.toString());

    // seed a api key
    ApiKey demoApiKey = new ApiKey("abc123", "demoApp", savedC.getId());
    ApiKey savedApiKey = apiKeyRepo.save(demoApiKey);

    log.info(savedApiKey.toString());

  }

  private String ensureBasePath() {
    // get file base file path
    Path basePath = Paths.get(fileStorageProperty.getUploadDir());

    // check if it is existing
    if (!Files.exists(basePath)) {
      try {
        Files.createDirectory(basePath);
      } catch (IOException e) {
        throw new RuntimeException("Error Creating Base File Path:" + basePath.toAbsolutePath(), e);
      }
    }
    return "Basepath is surely existing! [basePath]: " + basePath.toAbsolutePath();

  }
}
