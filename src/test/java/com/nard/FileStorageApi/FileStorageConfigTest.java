package com.nard.FileStorageApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nard.FileStorageApi.config.FileStorage;

@SpringBootTest
@ActiveProfiles("test")
public class FileStorageConfigTest {

  @Autowired
  private FileStorage fileStorage;

  @Test
  void testFileStorageConfig() {
    // Test profile uses /tmp/fileStorageApi-test, but if not active, it uses the main config
    String expectedPath = "/tmp/fileStorageApi-test";
    assertEquals(expectedPath, fileStorage.getRootStorage());
    assertEquals("50MB", fileStorage.getUploadMaxSize());
  }

}
