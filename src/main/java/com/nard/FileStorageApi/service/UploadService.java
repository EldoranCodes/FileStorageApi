package com.nard.FileStorageApi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.config.FileStorageProperty;
import com.nard.FileStorageApi.dto.ApiResponse;
import com.nard.FileStorageApi.model.ApiKey;
import com.nard.FileStorageApi.model.FileMetaData;
import com.nard.FileStorageApi.repository.FileMetaDataRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UploadService {

  private ApiKeyService apiKeyService;
  private FileMetaDataRepository fileMetaDataRepo;

  private final Logger log = LoggerFactory.getLogger(UploadService.class);

  private final Path baseFilePath;

  // constructor of this service and it sets the base file path on call
  public UploadService(FileStorageProperty fileStorageProperty, ApiKeyService apiKeyService,
      FileMetaDataRepository fileMetaDataRepo) {
    this.apiKeyService = apiKeyService;
    this.fileMetaDataRepo = fileMetaDataRepo;

    this.baseFilePath = Paths.get(fileStorageProperty.getUploadDir()).toAbsolutePath().normalize();
  }

  public ApiResponse uploadFile(String apiKey, MultipartFile file) {
    String originalName = file.getOriginalFilename();

    // validate originalName;
    if (!validateFilename(originalName)) {
      return ApiResponse.error("File name Contains invalid characters");
    }

    // validate the file extension
    String extension = "";
    int dotIndex = originalName.lastIndexOf(".");

    if (dotIndex > 0 && dotIndex < originalName.length() - 1) {
      extension = originalName.substring(dotIndex + 1).toLowerCase();
    }

    // lets create our storedFilename
    String storedFileName = getStoredFileName(extension);
    // valdiate file if has invalid name

    ApiKey apikeyObject = apiKeyService.validateApiKey(apiKey);

    if (apikeyObject == null) {
      return ApiResponse.error("Invalid Api Key!");
    }

    // workspace pattern:
    // {basepath}/{appName}/{date}/{uuid.ext}
    Path uploadDirectory = getUploadDirectory(apikeyObject);
    Path targetFilePath = uploadDirectory.resolve(storedFileName);
    log.info("[TargetFilePath] was created before copying the file: {}", targetFilePath);

    // upload the file
    try {
      Files.copy(file.getInputStream(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
      log.info("File successfully saved to: {}", targetFilePath);
    } catch (IOException e) {
      log.error("Error Saving File in the Target File Path", e);
      return ApiResponse.error("Error Storing file: " + e.getMessage());
    }

    // Save metadata to database
    FileMetaData metadata = new FileMetaData();
    metadata.setOriginalName(originalName);
    metadata.setStoredName(storedFileName);
    metadata.setFilePath(targetFilePath.toString());
    metadata.setCreatedAt(LocalDateTime.now());
    metadata.setOwner(apikeyObject.getOwner());
    metadata.setAppName(apikeyObject.getAppName());
    metadata.setFileSize(file.getSize());
    metadata.setContentType(file.getContentType());
    metadata.setDeletedAt(null);

    try {
      FileMetaData savedMetadata = fileMetaDataRepo.save(metadata);
      log.info("File metadata saved with ID: {}", savedMetadata.getId());
      return ApiResponse.ok("File uploaded successfully", savedMetadata);
    } catch (Exception e) {
      log.error("Error saving file metadata", e);
      // File is already saved, but metadata failed - this is a partial failure
      return ApiResponse.error("File uploaded but metadata save failed: " + e.getMessage());
    }
  }

  private String getStoredFileName(String extension) {
    String storedFileName = "";

    boolean exists = true;
    while (exists) {
      storedFileName = UUID.randomUUID().toString() + "." + extension;
      // check if storeFilenameExist
      exists = fileMetaDataRepo.existsByStoredName(storedFileName);
    }

    return storedFileName;
  }

  public boolean validateFilename(String originalName) {

    log.debug("VALIDATING THE FILENAME: {}", originalName);

    if (originalName == null || originalName.trim().isEmpty()) {
      log.debug("File name is invalid. filename is null or empty");
      return false;
    }

    // Check for path traversal
    if (originalName.contains("..")) {
      log.debug("File name is invalid. it has (..) in the filename");
      return false;
    }

    // Check for path separators
    if (originalName.contains("/") || originalName.contains("\\")) {
      log.debug("File name is invalid. it has (/) or (\\) in the filename");
      return false;
    }

    // Check for hidden files
    if (originalName.startsWith(".")) {

      log.debug("File name is invalid. it has (.) in the filename");
      return false;
    }

    return true;

  }

  private Path getUploadDirectory(ApiKey apikeyObject) {
    // the pattern of our filepaths will be:
    // {basepath}/appName/mm-dd-yyyy/
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));

    Path uploadPath = baseFilePath
        .resolve(apikeyObject.getAppName())
        .resolve(date);

    try {
      Files.createDirectories(uploadPath);
      return uploadPath;
    } catch (IOException e) {
      log.error("Error Creating Upload File Path Directory", e);
      throw new RuntimeException("Failed to create upload directory: " + uploadPath, e);
    }
  }

}
