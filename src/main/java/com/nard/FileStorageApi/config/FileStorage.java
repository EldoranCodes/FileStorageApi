package com.nard.FileStorageApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorage {

  // Matches file.root-storage
  private String rootStorage;

  // Matches file.upload.max-size
  private String uploadMaxSize;

}
