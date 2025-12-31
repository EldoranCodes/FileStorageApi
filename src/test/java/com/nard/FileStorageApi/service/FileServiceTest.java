package com.nard.FileStorageApi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.dto.FileMetadataDto;
import com.nard.FileStorageApi.model.Account;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;
import com.nard.FileStorageApi.model.User;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  @Mock
  private StoredFileRepository storedFileRepository;

  @InjectMocks
  private FileService fileService;

  @TempDir
  Path tempDir;

  private User testUser;
  private User otherUser;
  private Account testAccount;
  private UploadBatch testBatch;
  private StoredFile testFile;

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

    testFile = new StoredFile();
    testFile.setId(1L);
    testFile.setOriginalName("test.txt");
    testFile.setUuid("test-uuid");
    testFile.setBatch(testBatch);
    testFile.setStoragePath(tempDir.resolve("test.txt").toString());
    testFile.setUploadTimestamp(LocalDateTime.now());

    // Create test file on disk
    Files.createFile(tempDir.resolve("test.txt"));
  }

  @Test
  void testGetFileInfo_Success() {
    // Arrange
    when(storedFileRepository.findById(1L)).thenReturn(Optional.of(testFile));

    // Act
    FileMetadataDto result = fileService.getFileInfo(1L, testUser);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.getFileId());
    assertEquals("test.txt", result.getOriginalName());
    assertEquals("test-uuid", result.getUuid());
    assertNotNull(result.getUploadTimestamp());
  }

  @Test
  void testGetFileInfo_FileNotFound_ThrowsException() {
    // Arrange
    when(storedFileRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      fileService.getFileInfo(999L, testUser);
    });
  }

  @Test
  void testGetFileInfo_UnauthorizedUser_ThrowsException() {
    // Arrange
    when(storedFileRepository.findById(1L)).thenReturn(Optional.of(testFile));

    // Act & Assert
    assertThrows(SecurityException.class, () -> {
      fileService.getFileInfo(1L, otherUser);
    });
  }

  @Test
  void testDeleteFile_Success() {
    // Arrange
    when(storedFileRepository.findById(1L)).thenReturn(Optional.of(testFile));

    // Act
    DeleteResponseDto result = fileService.deleteFile(1L, testUser);

    // Assert
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().contains("deleted successfully"));
    
    verify(storedFileRepository, times(1)).delete(testFile);
  }

  @Test
  void testDeleteFile_FileNotFound_ThrowsException() {
    // Arrange
    when(storedFileRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      fileService.deleteFile(999L, testUser);
    });
  }

  @Test
  void testDeleteFile_UnauthorizedUser_ThrowsException() {
    // Arrange
    when(storedFileRepository.findById(1L)).thenReturn(Optional.of(testFile));

    // Act & Assert
    assertThrows(SecurityException.class, () -> {
      fileService.deleteFile(1L, otherUser);
    });
  }

  @Test
  void testDeleteFile_FileNotFoundOnDisk_StillDeletes() {
    // Arrange
    StoredFile missingFile = new StoredFile();
    missingFile.setId(2L);
    missingFile.setOriginalName("missing.txt");
    missingFile.setStoragePath("/nonexistent/path/missing.txt");
    missingFile.setBatch(testBatch);

    when(storedFileRepository.findById(2L)).thenReturn(Optional.of(missingFile));

    // Act
    DeleteResponseDto result = fileService.deleteFile(2L, testUser);

    // Assert
    assertNotNull(result);
    // The service returns success=true even if file doesn't exist on disk (it just logs an error)
    // This is the actual behavior - the file record is still deleted from DB
    assertTrue(result.isSuccess());
    verify(storedFileRepository, times(1)).delete(missingFile);
  }

}

