package com.nard.FileStorageApi;

import com.nard.FileStorageApi.service.FileManagementService;

import com.nard.FileStorageApi.repository.ClientsRepository;
import com.nard.FileStorageApi.repository.MetadataRepository;
import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.model.Metadata;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FileManagementTest {

  @Autowired
  private FileManagementService fileManagementService;

  @Autowired
  private MetadataRepository metadataRepo;

  @Autowired
  private ClientsRepository clientsRepository;

  @Test
  void shouldUploadFileAndSaveMetadata() throws Exception {

    // create client
    Client client = new Client();
    client.setApiKey("123zxc");
    // client.setAppName("TestApp");
    // client.setAdminName("nard_admin");

    Client savedClient = clientsRepository.save(client);
    Long clientId = savedClient.getClientId(); // use this in your upload test
    // GIVEN
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",
        "Hello World".getBytes());

    // WHEN
    fileManagementService.uploadFile(
        clientId,
        "billing",
        "invoices",
        file,
        "nard");

    // THEN (DB)
    assertEquals(1, metadataRepo.count());

    Metadata testesttsaved = metadataRepo.findAll().get(0);

    assertEquals("test.txt", saved.getOriginalFileName());
    assertEquals("nard", saved.getUploadedBy());
    assertNotNull(saved.getStoragePath());

    // THEN (filesystem)
    Path storedFile = Path.of(saved.getStoragePath());
    assertTrue(Files.exists(storedFile));
  }

  @AfterEach
  void cleanup() throws IOException {
    Path root = Path.of("target/test-uploads");
    if (Files.exists(root)) {
      Files.walk(root)
          .sorted((a, b) -> b.compareTo(a)) // delete children first
          .forEach(p -> p.toFile().delete());
    }
  }
}
