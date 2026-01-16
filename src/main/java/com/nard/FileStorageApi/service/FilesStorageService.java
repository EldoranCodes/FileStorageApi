package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.config.FileStorageProperty;
import com.nard.FileStorageApi.dto.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FilesStorageService {
  private final Logger log = LoggerFactory.getLogger(FilesStorageService.class);

  private final Path baseFilePath;

  private FileStorageProperty fileStorageProperty;

  // constructor of this service and it sets the base file path on call
  public FilesStorageService(FileStorageProperty fileStorageProperty) {
    this.fileStorageProperty = fileStorageProperty;

    this.baseFilePath = Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();
    try {
      if (!Files.exists(this.baseFilePath)) {
        Files.createDirectory(this.baseFilePath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not create the directory where uploaded fields wil lbe stored.", e);
    }
  }

  public String getFilePath() {
    return fileStorageProperty.getUploadDir();
  }

}
