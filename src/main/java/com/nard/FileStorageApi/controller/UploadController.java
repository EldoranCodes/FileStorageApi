package com.nard.FileStorageApi.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.dto.ApiResponse;
import com.nard.FileStorageApi.dto.FileListItemDto;
import com.nard.FileStorageApi.service.FileService;
import com.nard.FileStorageApi.service.UploadService;

@RestController
@RequestMapping("/upload")
public class UploadController {

  private UploadService uploadService;
  private FileService fileService;

  public UploadController(UploadService uploadService, FileService fileService) {
    this.uploadService = uploadService;
    this.fileService = fileService;
  }

  /**
   * Upload a file
   * POST /upload/file?apiKey=xxx
   */
  @PostMapping("/file")
  public ApiResponse uploadFile(@RequestParam String apiKey, @RequestParam MultipartFile file) {
    return uploadService.uploadFile(apiKey, file);
  }

  /**
   * Stream/download a file by stored name
   * GET /upload/file?apiKey=xxx&storedName=uuid.ext
   */
  @GetMapping("/file")
  public ResponseEntity<Resource> getFile(
      @RequestParam String apiKey,
      @RequestParam String storedName) {
    return fileService.streamFile(apiKey, storedName);
  }

  /**
   * List all files for an API key
   * GET /upload/files?apiKey=xxx
   */
  @GetMapping("/files")
  public ApiResponse listFiles(@RequestParam String apiKey) {
    List<FileListItemDto> files = fileService.listFilesWithUrls(apiKey);
    return ApiResponse.ok("Files retrieved successfully", files);
  }

  /**
   * Soft delete a file
   * DELETE /upload/file?apiKey=xxx&storedName=uuid.ext
   */
  @DeleteMapping("/file")
  public ApiResponse deleteFile(
      @RequestParam String apiKey,
      @RequestParam String storedName) {
    boolean deleted = fileService.softDeleteFile(apiKey, storedName);
    if (deleted) {
      return ApiResponse.ok("File deleted successfully");
    } else {
      return ApiResponse.error("File not found or invalid API key");
    }
  }

}
