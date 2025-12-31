package com.nard.FileStorageApi.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nard.FileStorageApi.dto.UploadResponseDto;
import com.nard.FileStorageApi.model.User;
import com.nard.FileStorageApi.service.UploadService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

  @Autowired
  private UploadService uploadService;

  @PostMapping
  public ResponseEntity<UploadResponseDto> uploadFiles(
      @RequestParam("files") MultipartFile[] files,
      @RequestAttribute("authenticatedUser") User user) {
    
    try {
      UploadResponseDto response = uploadService.uploadFiles(user, files);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Invalid request: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (IOException e) {
      log.error("Error uploading files", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}

