package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.dto.FileListItemDto;
import com.nard.FileStorageApi.model.ApiKey;
import com.nard.FileStorageApi.model.FileMetaData;
import com.nard.FileStorageApi.repository.FileMetaDataRepository;

@Service
public class FileService {

  private final Logger log = LoggerFactory.getLogger(FileService.class);
  private final FileMetaDataRepository fileMetaDataRepo;
  private final ApiKeyService apiKeyService;

  public FileService(FileMetaDataRepository fileMetaDataRepo, ApiKeyService apiKeyService) {
    this.fileMetaDataRepo = fileMetaDataRepo;
    this.apiKeyService = apiKeyService;
  }

  /**
   * Stream a file by stored name, validated with API key
   * 
   * @param apiKey     The API key for authentication
   * @param storedName The stored filename (UUID)
   * @return ResponseEntity with file resource or error
   */
  public ResponseEntity<Resource> streamFile(String apiKey, String storedName) {
    // Validate API key
    ApiKey apiKeyObject = apiKeyService.validateApiKey(apiKey);
    if (apiKeyObject == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Find file metadata
    Optional<FileMetaData> metadataOpt = fileMetaDataRepo
        .findByStoredNameAndOwnerAndDeletedAtIsNull(storedName, apiKeyObject.getOwner());

    if (metadataOpt.isEmpty()) {
      log.warn("File not found or deleted: storedName={}, owner={}", storedName, apiKeyObject.getOwner());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    FileMetaData metadata = metadataOpt.get();

    try {
      Path filePath = Paths.get(metadata.getFilePath());
      Resource resource = new UrlResource(filePath.toUri());

      if (!resource.exists() || !resource.isReadable()) {
        log.error("File is not readable: {}", filePath);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      // Determine content type
      String contentType = metadata.getContentType();
      if (contentType == null || contentType.isEmpty()) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "inline; filename=\"" + metadata.getOriginalName() + "\"")
          .body(resource);

    } catch (IOException e) {
      log.error("Error reading file: {}", metadata.getFilePath(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * List all files for an API key (filtered by owner)
   * 
   * @param apiKey The API key for authentication
   * @return List of FileMetaData
   */
  public List<FileMetaData> listFiles(String apiKey) {
    // Validate API key
    ApiKey apiKeyObject = apiKeyService.validateApiKey(apiKey);
    if (apiKeyObject == null) {
      return List.of();
    }

    // Return files filtered by owner and appName (since 1 apikey = 1 app)
    return fileMetaDataRepo.findByOwnerAndAppNameAndDeletedAtIsNull(
        apiKeyObject.getOwner(),
        apiKeyObject.getAppName());
  }

  /**
   * Same as listFiles(), but returns DTO with stream URL template.
   */
  public List<FileListItemDto> listFilesWithUrls(String apiKey) {
    List<FileMetaData> files = listFiles(apiKey);
    if (files == null || files.isEmpty()) {
      return List.of();
    }

    List<FileListItemDto> items = new java.util.ArrayList<FileListItemDto>();
    for (int i = 0; i < files.size(); i++) {
      FileMetaData m = files.get(i);
      FileListItemDto dto = new FileListItemDto();
      dto.setId(m.getId());
      dto.setOriginalName(m.getOriginalName());
      dto.setStoredName(m.getStoredName());
      dto.setContentType(m.getContentType());
      dto.setFileSize(m.getFileSize());
      dto.setCreatedAt(m.getCreatedAt());
      dto.setStreamUrl("/upload/file?storedName=" + m.getStoredName());
      items.add(dto);
    }

    return items;
  }

  /**
   * Soft delete a file (set deletedAt timestamp)
   * 
   * @param apiKey     The API key for authentication
   * @param storedName The stored filename (UUID)
   * @return true if deleted, false otherwise
   */
  public boolean softDeleteFile(String apiKey, String storedName) {
    // Validate API key
    ApiKey apiKeyObject = apiKeyService.validateApiKey(apiKey);
    if (apiKeyObject == null) {
      return false;
    }

    // Find file metadata
    Optional<FileMetaData> metadataOpt = fileMetaDataRepo
        .findByStoredNameAndOwnerAndDeletedAtIsNull(storedName, apiKeyObject.getOwner());

    if (metadataOpt.isEmpty()) {
      return false;
    }

    FileMetaData metadata = metadataOpt.get();
    metadata.setDeletedAt(LocalDateTime.now());
    fileMetaDataRepo.save(metadata);

    log.info("File soft deleted: storedName={}, owner={}", storedName, apiKeyObject.getOwner());
    return true;
  }

  /**
   * Permanently delete files that have been marked for deletion
   * This should be called periodically (e.g., via scheduled task)
   * 
   * @return Number of files permanently deleted
   */
  public int cleanupDeletedFiles() {
    List<FileMetaData> deletedFiles = fileMetaDataRepo.findByDeletedAtIsNotNull();
    int deletedCount = 0;

    for (FileMetaData metadata : deletedFiles) {
      try {
        Path filePath = Paths.get(metadata.getFilePath());
        if (Files.exists(filePath)) {
          Files.delete(filePath);
          log.info("Permanently deleted file: {}", filePath);
        }
        // Remove metadata from database
        fileMetaDataRepo.delete(metadata);
        deletedCount++;
      } catch (IOException e) {
        log.error("Error deleting file: {}", metadata.getFilePath(), e);
      }
    }

    log.info("Cleanup completed: {} files permanently deleted", deletedCount);
    return deletedCount;
  }
}
