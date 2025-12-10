package com.nard.FileStorageApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nard.FileStorageApi.config.FileStorageConfig;

@SpringBootTest
public class FileStorageConfigTest {

  @Autowired
  private FileStorageConfig fileStorageConfig;

  @Test
  void testFileStorageConfig() {
    assertEquals("/home/nard/myProj/FileStorageApi/dev_file", fileStorageConfig.getRootStorage());
    assertEquals("50MB", fileStorageConfig.getUploadMaxSize());
  }

}
