package com.nard.FileStorageApi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FileMetaData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String originalName;

  private String storedName;

  private String filePath;

  private LocalDateTime createdAt;

  private Long owner; // Consumer ID

  private String appName; // From ApiKey

  private Long fileSize; // File size in bytes

  private String contentType; // MIME type

  private LocalDateTime deletedAt; // For soft delete

}
