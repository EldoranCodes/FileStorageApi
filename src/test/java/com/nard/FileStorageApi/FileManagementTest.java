package com.nard.FileStorageApi;

import com.nard.FileStorageApi.config.TestDataConfig;
import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.model.Metadata;
import com.nard.FileStorageApi.repository.MetadataRepository;
import com.nard.FileStorageApi.service.FileManagementService;
import com.nard.FileStorageApi.testutil.TestFiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataConfig.class)
class FileManagementTest {

  @Autowired
  private FileManagementService fileManagementService;

  @Autowired
  private MetadataRepository metadataRepo;

  @Autowired
  private Client testClient;

  @Test
  void shouldUploadTxtFileAndSaveMetadata() throws Exception {

    long beforeCount = metadataRepo.count();

    Metadata metadata = fileManagementService.uploadFile(
        testClient.getClientId(),
        "todoApp",
        "attachment",
        TestFiles.txt(),
        "todoApp_user1");

    assertNotNull(metadata, "Metadata must not be null");
    assertEquals(
        "test.txt",
        metadata.getOriginalFileName(),
        "Original filename mismatch");

    assertEquals(
        beforeCount + 1,
        metadataRepo.count(),
        "Exactly one metadata record should be added");
  }

  @Test
  void shouldUploadPdfFile() throws Exception {
    Metadata metadata = fileManagementService.uploadFile(
        testClient.getClientId(),
        "todoApp",
        "attachment",
        TestFiles.pdf(),
        "todoApp_user1");

    assertEquals("test.pdf", metadata.getOriginalFileName());
  }

  @Test
  void shouldUploadExcelFile() throws Exception {
    Metadata metadata = fileManagementService.uploadFile(
        testClient.getClientId(),
        "todoApp",
        "attachment",
        TestFiles.excel(),
        "todoApp_user1");

    assertEquals("test.xlsx", metadata.getOriginalFileName());
  }

  @AfterEach
  void cleanupStorage() throws IOException {
    Path root = Path.of("storage-test"); // or from config
    if (Files.exists(root)) {
      Files.walk(root)
          .sorted((a, b) -> b.compareTo(a))
          .forEach(p -> p.toFile().delete());
    }
  }
}
