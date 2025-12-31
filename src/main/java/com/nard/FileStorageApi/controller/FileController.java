package com.nard.FileStorageApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.model.User;
import com.nard.FileStorageApi.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
public class FileController {

  @Autowired
  private FileService fileService;

  @DeleteMapping("/delete/file/{fileId}")
  public ResponseEntity<DeleteResponseDto> deleteFile(
      @PathVariable Long fileId,
      @RequestAttribute("authenticatedUser") User user) {
    
    try {
      DeleteResponseDto response = fileService.deleteFile(fileId, user);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("File not found: {}", fileId);
      return ResponseEntity.notFound().build();
    } catch (SecurityException e) {
      log.error("Unauthorized access: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

}

