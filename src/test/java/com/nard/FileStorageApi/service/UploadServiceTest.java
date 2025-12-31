package com.nard.FileStorageApi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.config.FileStorage;
import com.nard.FileStorageApi.dao.StoredFileRepository;
import com.nard.FileStorageApi.dao.UploadBatchRepository;
import com.nard.FileStorageApi.dto.UploadResponseDto;
import com.nard.FileStorageApi.model.Account;
import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;
import com.nard.FileStorageApi.model.User;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UploadServiceTest {

  @Mock
  private FileStorage fileStorage;

  @Mock
  private UploadBatchRepository uploadBatchRepository;

  @Mock
  private StoredFileRepository storedFileRepository;

  @Mock
  private MultipartFile multipartFile;

  @InjectMocks
  private UploadService uploadService;

  @TempDir
  Path tempDir;

  private User testUser;
  private Account testAccount;
  private UploadBatch testBatch;

  @BeforeEach
  void setUp() {
    testAccount = new Account();
    testAccount.setId(1L);
    testAccount.setName("Test Account");

    testUser = new User();
    testUser.setId(1L);
    testUser.setName("test-user");
    testUser.setStatus("ACTIVE");
    testUser.setAccount(testAccount);

    testBatch = new UploadBatch();
    testBatch.setId(1L);
    testBatch.setUser(testUser);
    testBatch.setCreatedAt(LocalDateTime.now());
    testBatch.setStatus("PENDING");

    when(fileStorage.getRootStorage()).thenReturn(tempDir.toString());
  }

  @Test
  void testUploadFiles_SingleFile_Success() throws IOException {
    // Arrange
    String fileName = "test.txt";
    byte[] content = "test content".getBytes();
    
    when(multipartFile.isEmpty()).thenReturn(false);
    when(multipartFile.getOriginalFilename()).thenReturn(fileName);
    when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));
    
    MultipartFile[] files = {multipartFile};
    
    when(uploadBatchRepository.save(any(UploadBatch.class))).thenReturn(testBatch);
    
    StoredFile storedFile = new StoredFile();
    storedFile.setId(1L);
    storedFile.setOriginalName(fileName);
    storedFile.setUuid("test-uuid");
    storedFile.setBatch(testBatch);
    storedFile.setUploadTimestamp(LocalDateTime.now());
    
    when(storedFileRepository.save(any(StoredFile.class))).thenReturn(storedFile);

    // Act
    UploadResponseDto result = uploadService.uploadFiles(testUser, files);

    // Assert
    assertNotNull(result);
    assertEquals(testBatch.getId(), result.getBatchId());
    assertEquals("SUCCESS", result.getStatus());
    assertEquals(1, result.getFiles().size());
    assertEquals(fileName, result.getFiles().get(0).getOriginalName());
    
    verify(uploadBatchRepository, times(2)).save(any(UploadBatch.class));
    verify(storedFileRepository, times(1)).save(any(StoredFile.class));
  }

  @Test
  void testUploadFiles_MultipleFiles_Success() throws IOException {
    // Arrange
    MultipartFile file1 = mock(MultipartFile.class);
    MultipartFile file2 = mock(MultipartFile.class);
    
    when(file1.isEmpty()).thenReturn(false);
    when(file1.getOriginalFilename()).thenReturn("file1.txt");
    when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("content1".getBytes()));
    
    when(file2.isEmpty()).thenReturn(false);
    when(file2.getOriginalFilename()).thenReturn("file2.txt");
    when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("content2".getBytes()));
    
    MultipartFile[] files = {file1, file2};
    
    when(uploadBatchRepository.save(any(UploadBatch.class))).thenReturn(testBatch);
    
    StoredFile storedFile1 = new StoredFile();
    storedFile1.setId(1L);
    storedFile1.setOriginalName("file1.txt");
    storedFile1.setUuid("uuid1");
    storedFile1.setBatch(testBatch);
    storedFile1.setUploadTimestamp(LocalDateTime.now());
    
    StoredFile storedFile2 = new StoredFile();
    storedFile2.setId(2L);
    storedFile2.setOriginalName("file2.txt");
    storedFile2.setUuid("uuid2");
    storedFile2.setBatch(testBatch);
    storedFile2.setUploadTimestamp(LocalDateTime.now());
    
    when(storedFileRepository.save(any(StoredFile.class)))
        .thenReturn(storedFile1)
        .thenReturn(storedFile2);

    // Act
    UploadResponseDto result = uploadService.uploadFiles(testUser, files);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getFiles().size());
    assertEquals("SUCCESS", result.getStatus());
    
    verify(storedFileRepository, times(2)).save(any(StoredFile.class));
  }

  @Test
  void testUploadFiles_NoFiles_ThrowsException() {
    // Arrange
    MultipartFile[] files = null;

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      uploadService.uploadFiles(testUser, files);
    });
  }

  @Test
  void testUploadFiles_EmptyArray_ThrowsException() {
    // Arrange
    MultipartFile[] files = {};

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      uploadService.uploadFiles(testUser, files);
    });
  }

  @Test
  void testUploadFiles_EmptyFile_SkipsFile() throws IOException {
    // Arrange
    when(multipartFile.isEmpty()).thenReturn(true);
    MultipartFile[] files = {multipartFile};
    
    when(uploadBatchRepository.save(any(UploadBatch.class))).thenReturn(testBatch);

    // Act
    UploadResponseDto result = uploadService.uploadFiles(testUser, files);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getFiles().size());
    assertEquals("SUCCESS", result.getStatus());
    verify(storedFileRepository, never()).save(any(StoredFile.class));
  }

}

