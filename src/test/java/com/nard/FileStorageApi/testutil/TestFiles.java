
package com.nard.FileStorageApi.testutil;

import org.springframework.mock.web.MockMultipartFile;

public final class TestFiles {

  private TestFiles() {
  }

  public static MockMultipartFile txt() {
    return new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",
        "Hello World".getBytes());
  }

  public static MockMultipartFile pdf() {
    return new MockMultipartFile(
        "file",
        "test.pdf",
        "application/pdf",
        "%PDF-1.4 fake pdf content".getBytes());
  }

  public static MockMultipartFile excel() {
    return new MockMultipartFile(
        "file",
        "test.xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "fake excel content".getBytes());
  }
}
