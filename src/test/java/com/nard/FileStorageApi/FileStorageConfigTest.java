package com.nard.FileStorageApi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nard.FileStorageApi.config.FileStorage;

@SpringBootTest
public class FileStorageConfigTest {

  @Autowired
  private FileStorage fileStorage;

  @Test
  void testFileStorageConfig() {
    assertEquals("/home/nard/myProj/FileStorageApi/dev_file", fileStorage.getRootStorage());
    assertEquals("50MB", fileStorage.getUploadMaxSize());
  }

}
