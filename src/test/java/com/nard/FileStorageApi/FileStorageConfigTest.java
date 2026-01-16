package com.nard.FileStorageApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nard.FileStorageApi.config.FileStorageProperty;

@SpringBootTest
public class FileStorageConfigTest {

  @Autowired
  private FileStorageProperty fileStorageProperty;

  @Test
  void testFileStorageConfig() {
    assertEquals("/uploads", fileStorageProperty.getUploadDir());

    assertEquals("50MB", fileStorageProperty.getUploadMaxSize());
  }

}
