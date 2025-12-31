package com.nard.FileStorageApi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dao.UploadBatchRepository;
import com.nard.FileStorageApi.dto.BatchFilesResponseDto;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.model.Account;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;
import com.nard.FileStorageApi.model.User;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

  @Mock
  private UploadBatchRepository uploadBatchRepository;

  @Mock
  private StoredFileRepository storedFileRepository;

  @InjectMocks
  private BatchService batchService;

  @TempDir
  Path tempDir;

  private User testUser;
  private User otherUser;
  private Account testAccount;
  private UploadBatch testBatch;
  private StoredFile testFile1;
  private StoredFile testFile2;

  @BeforeEach
  void setUp() throws IOException {
    testAccount = new Account();
    testAccount.setId(1L);
    testAccount.setName("Test Account");

    testUser = new User();
    testUser.setId(1L);
    testUser.setName("test-user");
    testUser.setStatus("ACTIVE");
    testUser.setAccount(testAccount);

    otherUser = new User();
    otherUser.setId(2L);
    otherUser.setName("other-user");
    otherUser.setStatus("ACTIVE");

    testBatch = new UploadBatch();
    testBatch.setId(1L);
    testBatch.setUser(testUser);
    testBatch.setCreatedAt(LocalDateTime.now());
    testBatch.setStatus("SUCCESS");

    testFile1 = new StoredFile();
    testFile1.setId(1L);
    testFile1.setOriginalName("file1.txt");
    testFile1.setUuid("uuid1");
    testFile1.setBatch(testBatch);
    testFile1.setStoragePath(tempDir.resolve("file1.txt").toString());
    testFile1.setUploadTimestamp(LocalDateTime.now());

    testFile2 = new StoredFile();
    testFile2.setId(2L);
    testFile2.setOriginalName("file2.txt");
    testFile2.setUuid("uuid2");
    testFile2.setBatch(testBatch);
    testFile2.setStoragePath(tempDir.resolve("file2.txt").toString());
    testFile2.setUploadTimestamp(LocalDateTime.now());

    // Create test files on disk
    Files.createFile(tempDir.resolve("file1.txt"));
    Files.createFile(tempDir.resolve("file2.txt"));
  }

  @Test
  void testGetBatchFiles_Success() {
    // Arrange
    when(uploadBatchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
    when(storedFileRepository.findByBatch(testBatch)).thenReturn(Arrays.asList(testFile1, testFile2));

    // Act
    BatchFilesResponseDto result = batchService.getBatchFiles(1L, testUser);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getBatchId());
    assertEquals("SUCCESS", result.getStatus());
    assertEquals(2, result.getFiles().size());
    assertEquals("file1.txt", result.getFiles().get(0).getOriginalName());
    assertEquals("file2.txt", result.getFiles().get(1).getOriginalName());
  }

  @Test
  void testGetBatchFiles_BatchNotFound_ThrowsException() {
    // Arrange
    when(uploadBatchRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      batchService.getBatchFiles(999L, testUser);
    });
  }

  @Test
  void testGetBatchFiles_UnauthorizedUser_ThrowsException() {
    // Arrange
    when(uploadBatchRepository.findById(1L)).thenReturn(Optional.of(testBatch));

    // Act & Assert
    assertThrows(SecurityException.class, () -> {
      batchService.getBatchFiles(1L, otherUser);
    });
  }

  @Test
  void testDeleteBatch_Success() {
    // Arrange
    when(uploadBatchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
    when(storedFileRepository.findByBatch(testBatch)).thenReturn(Arrays.asList(testFile1, testFile2));

    // Act
    DeleteResponseDto result = batchService.deleteBatch(1L, testUser);

    // Assert
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().contains("deleted successfully"));
    
    verify(storedFileRepository, times(1)).deleteAll(anyList());
    verify(uploadBatchRepository, times(1)).delete(testBatch);
  }

  @Test
  void testDeleteBatch_BatchNotFound_ThrowsException() {
    // Arrange
    when(uploadBatchRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      batchService.deleteBatch(999L, testUser);
    });
  }

  @Test
  void testDeleteBatch_UnauthorizedUser_ThrowsException() {
    // Arrange
    when(uploadBatchRepository.findById(1L)).thenReturn(Optional.of(testBatch));

    // Act & Assert
    assertThrows(SecurityException.class, () -> {
      batchService.deleteBatch(1L, otherUser);
    });
  }

  @Test
  void testDeleteBatch_FileNotFoundOnDisk_StillDeletes() {
    // Arrange
    StoredFile missingFile = new StoredFile();
    missingFile.setId(3L);
    missingFile.setOriginalName("missing.txt");
    missingFile.setStoragePath("/nonexistent/path/missing.txt");
    missingFile.setBatch(testBatch);

    when(uploadBatchRepository.findById(1L)).thenReturn(Optional.of(testBatch));
    when(storedFileRepository.findByBatch(testBatch)).thenReturn(Arrays.asList(missingFile));

    // Act
    DeleteResponseDto result = batchService.deleteBatch(1L, testUser);

    // Assert
    assertNotNull(result);
    assertTrue(result.isSuccess()); // Still succeeds even if file missing on disk
    verify(storedFileRepository, times(1)).deleteAll(anyList());
  }

}

