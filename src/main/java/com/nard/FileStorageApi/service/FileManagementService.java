package com.nard.FileStorageApi.service;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.config.FileStorageConfig;
import com.nard.FileStorageApi.dto.FileUploadResponse;
import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.model.Metadata;
import com.nard.FileStorageApi.repository.ClientsRepository;
import com.nard.FileStorageApi.repository.MetadataRepository;

@Service
public class FileManagementService {

  @Autowired
  private MetadataRepository metadataRepo;

  @Autowired
  private FileStorageConfig fileStorageConfig;

  // upload single file
  public Metadata uploadFile(Long clientId, String appName, String module,
      MultipartFile originalFile, String uploadedBy) {
    Metadata savedFileMetadata = null;

    // FileUploadResponse fileUploadResponse = new FileUploadResponse(false, "",
    // null);

    Metadata metadata = new Metadata();
    metadata.setClientId(clientId);
    // 1. get the orignal name
    String originalFileName = originalFile.getOriginalFilename();
    metadata.setOriginalFileName(originalFileName);

    // 2. creates the filepath Directory only
    String destinationFilePath = pathResolver(clientId, appName, module);

    // directory plus the uniquename
    Path storagePath = getStoragePath(originalFile, destinationFilePath);

    // save the file
    try {
      originalFile.transferTo(storagePath.toFile());
      metadata.setStoragePath(storagePath.toString());
    } catch (IllegalStateException | IOException e) {
      System.err.println("Failed to save file: " + e.getMessage());
      e.printStackTrace();
      // TODO: return a failed Uplaod Response then dont save inthe database
    }

    if (metadata.getStoragePath() != null && !metadata.getStoragePath().isBlank()) {
      // 3. set uploadedby
      metadata.setUploadedBy(uploadedBy);
      // 4. save the metadata in db and return a response
      try {
        savedFileMetadata = metadataRepo.save(metadata);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return savedFileMetadata;
  }

  private Path getStoragePath(MultipartFile originalFile, String destinationFilePath) {
    // 1. setting the original file name
    String originalFileName = originalFile.getOriginalFilename();
    // 2. getting the original file path, for metadata
    // make sure its unique
    String newUniqueFilename = generateUniqueFilename(originalFileName);
    // get the dir path
    Path destinationDirPath = Paths.get(destinationFilePath);
    // append the uniquef filename
    Path savedFilePath = destinationDirPath.resolve(newUniqueFilename);
    return savedFilePath;
  }

  private String generateUniqueFilename(String originalFileName) {

    String baseName = FilenameUtils.getBaseName(originalFileName);
    String extension = FilenameUtils.getExtension(originalFileName);
    String uuidNoDashes = UUID.randomUUID().toString().replace("-", "");
    String newUniqueFilename = baseName + "_" + uuidNoDashes + "." + extension;

    return newUniqueFilename;
  }

  private String pathResolver(Long clientId, String appName, String module) {
    // the format:
    // * Save files to `./<base-path>/<API_CLIENT_ID>/<appName>/<appName>/filename`
    String basePath = fileStorageConfig.getRootStorage();

    Path destinationDirPath = Paths.get(basePath);
    destinationDirPath = destinationDirPath.resolve(String.valueOf(clientId));

    if (appName != null && !appName.isBlank()) {
      destinationDirPath = destinationDirPath.resolve(appName);
    }

    if (module != null && !module.isBlank()) {
      destinationDirPath = destinationDirPath.resolve(module);
    }

    try {
      Files.createDirectories(destinationDirPath);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return destinationDirPath.toString();
  }

}
