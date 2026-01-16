package com.nard.FileStorageApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperty {

  // Matches file.upload-dir in applicaiton properties
  private String uploadDir;

  // Matches file.upload.max-size
  private String uploadMaxSize;

}
