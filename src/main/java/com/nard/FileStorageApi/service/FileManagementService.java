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

@Service
public class FileManagementService {

  @Autowired
  private ClientsRepository clientsRepository;

  // upload single file
  public void uploadFile(int clientId, String appName, String module,
      MultipartFile originalFile, String uploadedBy) {

    FileUploadResponse fileUploadResponse = new FileUploadResponse(false, "", null);

    Metadata metadata = new Metadata();
    // 1. get the orignal name
    String originalFileName = originalFile.getOriginalFilename();
    metadata.setOriginalFileName(originalFileName);

    // 2. get the storage path
    String destinationFilePath = pathResolver(clientId, appName, module);
    Path storagePath = getStoragePath(originalFile, destinationFilePath);
    // save it
    try {
      originalFile.transferTo(storagePath.toFile());
      metadata.setStoragePath(storagePath);
    } catch (IllegalStateException | IOException e) {
      System.err.println("Failed to save file: " + e.getMessage());
      e.printStackTrace();
      // TODO: return a failed Uplaod Response
    }

    // 3. set uploadedby
    metadata.setUploadedBy(uploadedBy);

    // retrun the metadata
    // return fileUploadResponse;
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

  private String saveFile(String newUniqueFilename, String desti  nationFilePath) {
    Path destinationDirPath = Paths.get(destinationFilePath);

    Path savedFilePath = destinationDirPath.resolve(newUniqueFilename);
    // get the uniquiname
    originalFile.transferTo(new File(destinationFilePath, newUniqueFilename));

    throw new UnsupportedOperationException("Unimplemented method 'saveFile'");
  }

  private String generateUniqueFilename(String originalFileName) {
    // TODO Auto-generated method stub
    // genereate a new UIID then append the extension of the original file name

    String baseName = FilenameUtils.getBaseName(originalFileName);
    String extension = FilenameUtils.getExtension(originalFileName);

    String uuidNoDashes = UUID.randomUUID().toString().replace("-", "");

    String newUniqueFilename = uuidNoDashes + extension;
    return newUniqueFilename;
  }

  private String pathResolver(int clientId, String appName, String module) {
    // TODO Auto-generated method stub
    // 1. generate file paths from root folder
    // 2. then check if not exist create it then return it
    //
    // the format:
    // * Save files to `./<base-path>/<API_CLIENT_ID>/<appName>/<appName>/filename`
    throw new UnsupportedOperationException("Unimplemented method 'pathResolver'");
  }

  // helper Method
  // upload a file
  // private void saveFile(MultipartFile uploadedFile, String clientId, String
  // category) throws IOException {
  //
  // // // plact it in a temporary folder
  // // File tempDir = new File(System.getProperty("java.io.tmpdir"));
  // // File tempFile = new File(tempDir, UUID.randomUUID().toString() + ".tmp");
  // // uploadedFile.transferTo(tempFile);
  // //
  // // String originalFileName = uploadedFile.getName().toString();
  // // Path baseStoragePath = Paths.get(fileStorageConfig.getRootStorage());
  // // String path = "";
  // //
  // // Path clientPath = baseStoragePath.resolve(String.valueOf(clientId));
  // // Path categoryPath = clientPath.resolve(category);
  // // Files.createDirectories(categoryPath);
  // //
  // // Path fullFilePath = categoryPath.resolve(originalFileName);
  // //
  // // int count = 0;
  // // checks if it already exist
  //
  // }

  // uplaod batch file

  // get single file

  // get batch files

  // search file

}
