package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.config.FileStorage;
import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dao.UploadBatchRepository;
import com.nard.FileStorageApi.dto.FileMetadataDto;
import com.nard.FileStorageApi.dto.UploadResponseDto;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;
import com.nard.FileStorageApi.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadService {

  @Autowired
  private FileStorage fileStorage;

  @Autowired
  private UploadBatchRepository uploadBatchRepository;

  @Autowired
  private StoredFileRepository storedFileRepository;

  /**
   * Uploads files for a user and creates a batch
   * @param user The authenticated user
   * @param files Array of files to upload
   * @return UploadResponseDto with batch info and file metadata
   */
  public UploadResponseDto uploadFiles(User user, MultipartFile[] files) throws IOException {
    if (files == null || files.length == 0) {
      throw new IllegalArgumentException("No files provided");
    }

    // Create upload batch
    UploadBatch batch = new UploadBatch();
    batch.setUser(user);
    batch.setCreatedAt(LocalDateTime.now());
    batch.setStatus("PENDING");
    batch = uploadBatchRepository.save(batch);

    List<FileMetadataDto> fileMetadataList = new ArrayList<>();
    String basePath = fileStorage.getRootStorage();
    Path userDir = Paths.get(basePath, "users", user.getName());

    // Create user directory if it doesn't exist
    if (!Files.exists(userDir)) {
      Files.createDirectories(userDir);
    }

    boolean allSuccess = true;

    try {
      // Process each file
      for (MultipartFile file : files) {
        if (file.isEmpty()) {
          continue;
        }

        try {
          // Generate UUID for file
          String uuid = UUID.randomUUID().toString();
          Path filePath = userDir.resolve(uuid);

          // Save file to disk
          Files.copy(file.getInputStream(), filePath);

          // Create stored file record
          StoredFile storedFile = new StoredFile();
          storedFile.setOriginalName(file.getOriginalFilename());
          storedFile.setUuid(uuid);
          storedFile.setBatch(batch);
          storedFile.setStoragePath(filePath.toString());
          storedFile.setUploadTimestamp(LocalDateTime.now());
          storedFile = storedFileRepository.save(storedFile);

          // Create metadata DTO
          FileMetadataDto metadata = FileMetadataDto.builder()
              .fileId(storedFile.getId())
              .originalName(storedFile.getOriginalName())
              .path("users/" + user.getName() + "/" + uuid)
              .uuid(storedFile.getUuid())
              .uploadTimestamp(storedFile.getUploadTimestamp().toString())
              .build();

          fileMetadataList.add(metadata);
          log.info("File uploaded successfully: {} -> {}", file.getOriginalFilename(), uuid);

        } catch (Exception e) {
          log.error("Error uploading file: {}", file.getOriginalFilename(), e);
          allSuccess = false;
        }
      }

      // Update batch status
      batch.setStatus(allSuccess ? "SUCCESS" : "FAILED");
      uploadBatchRepository.save(batch);

    } catch (Exception e) {
      log.error("Error during batch upload", e);
      batch.setStatus("FAILED");
      uploadBatchRepository.save(batch);
      throw e;
    }

    // Build response
    return UploadResponseDto.builder()
        .batchId(batch.getId())
        .status(batch.getStatus())
        .files(fileMetadataList)
        .build();
  }

}

