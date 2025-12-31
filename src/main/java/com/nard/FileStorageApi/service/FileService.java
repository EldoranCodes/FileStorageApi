package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.dto.FileMetadataDto;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {

  @Autowired
  private StoredFileRepository storedFileRepository;

  /**
   * Get file metadata by ID
   * @param fileId The file ID
   * @param user The authenticated user (for authorization)
   * @return FileMetadataDto
   */
  public FileMetadataDto getFileInfo(Long fileId, User user) {
    StoredFile file = storedFileRepository.findById(fileId)
        .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));

    // Verify ownership through batch
    if (file.getBatch() == null || file.getBatch().getUser() == null || 
        user == null || !file.getBatch().getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized access to file: " + fileId);
    }

    return FileMetadataDto.builder()
        .fileId(file.getId())
        .originalName(file.getOriginalName())
        .path(file.getStoragePath())
        .uuid(file.getUuid())
        .uploadTimestamp(file.getUploadTimestamp() != null ? file.getUploadTimestamp().toString() : null)
        .build();
  }

  /**
   * Delete a single file
   * @param fileId The file ID
   * @param user The authenticated user (for authorization)
   * @return DeleteResponseDto
   */
  public DeleteResponseDto deleteFile(Long fileId, User user) {
    StoredFile file = storedFileRepository.findById(fileId)
        .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));

    // Verify ownership through batch
    if (file.getBatch() == null || file.getBatch().getUser() == null || 
        user == null || !file.getBatch().getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized access to file: " + fileId);
    }

    // Delete file from disk
    boolean deletedFromDisk = true;
    try {
      Path filePath = Path.of(file.getStoragePath());
      if (Files.exists(filePath)) {
        Files.delete(filePath);
        log.info("Deleted file from disk: {}", file.getStoragePath());
      }
    } catch (IOException e) {
      log.error("Error deleting file from disk: {}", file.getStoragePath(), e);
      deletedFromDisk = false;
    }

    // Delete file record from database
    storedFileRepository.delete(file);

    return DeleteResponseDto.builder()
        .success(deletedFromDisk)
        .message(deletedFromDisk ? "File deleted successfully" : "File record deleted but disk deletion failed")
        .build();
  }

}

