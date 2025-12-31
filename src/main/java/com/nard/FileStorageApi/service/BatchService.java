package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dao.UploadBatchRepository;
import com.nard.FileStorageApi.dto.BatchFilesResponseDto;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.dto.FileMetadataDto;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;
import com.nard.FileStorageApi.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BatchService {

  @Autowired
  private UploadBatchRepository uploadBatchRepository;

  @Autowired
  private StoredFileRepository storedFileRepository;

  /**
   * Get all files in a batch
   * @param batchId The batch ID
   * @param user The authenticated user (for authorization)
   * @return BatchFilesResponseDto with batch info and files
   */
  public BatchFilesResponseDto getBatchFiles(Long batchId, User user) {
    UploadBatch batch = uploadBatchRepository.findById(batchId)
        .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));

    // Verify ownership
    if (batch.getUser() == null || user == null || !batch.getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized access to batch: " + batchId);
    }

    List<StoredFile> files = storedFileRepository.findByBatch(batch);
    
    List<FileMetadataDto> fileMetadataList = files.stream()
        .map(file -> FileMetadataDto.builder()
            .fileId(file.getId())
            .originalName(file.getOriginalName())
            .path(file.getStoragePath())
            .uuid(file.getUuid())
            .uploadTimestamp(file.getUploadTimestamp() != null ? file.getUploadTimestamp().toString() : null)
            .build())
        .collect(Collectors.toList());

    return BatchFilesResponseDto.builder()
        .batchId(batch.getId())
        .status(batch.getStatus())
        .files(fileMetadataList)
        .build();
  }

  /**
   * Delete a batch and all associated files
   * @param batchId The batch ID
   * @param user The authenticated user (for authorization)
   * @return DeleteResponseDto
   */
  public DeleteResponseDto deleteBatch(Long batchId, User user) {
    UploadBatch batch = uploadBatchRepository.findById(batchId)
        .orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));

    // Verify ownership
    if (batch.getUser() == null || user == null || !batch.getUser().getId().equals(user.getId())) {
      throw new SecurityException("Unauthorized access to batch: " + batchId);
    }

    List<StoredFile> files = storedFileRepository.findByBatch(batch);
    boolean allDeleted = true;

    // Delete files from disk
    for (StoredFile file : files) {
      try {
        Path filePath = Path.of(file.getStoragePath());
        if (Files.exists(filePath)) {
          Files.delete(filePath);
          log.info("Deleted file from disk: {}", file.getStoragePath());
        }
      } catch (IOException e) {
        log.error("Error deleting file from disk: {}", file.getStoragePath(), e);
        allDeleted = false;
      }
    }

    // Delete file records from database
    storedFileRepository.deleteAll(files);

    // Delete batch
    uploadBatchRepository.delete(batch);

    return DeleteResponseDto.builder()
        .success(allDeleted)
        .message(allDeleted ? "Batch and all files deleted successfully" : "Batch deleted with some file deletion errors")
        .build();
  }

}

