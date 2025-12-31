package com.nard.FileStorageApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nard.FileStorageApi.dto.BatchFilesResponseDto;
import com.nard.FileStorageApi.dto.DeleteResponseDto;
import com.nard.FileStorageApi.model.User;
import com.nard.FileStorageApi.service.BatchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping
public class BatchController {

  @Autowired
  private BatchService batchService;

  @GetMapping("/files/{batchId}")
  public ResponseEntity<BatchFilesResponseDto> getBatchFiles(
      @PathVariable Long batchId,
      @RequestAttribute("authenticatedUser") User user) {
    
    try {
      BatchFilesResponseDto response = batchService.getBatchFiles(batchId, user);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Batch not found: {}", batchId);
      return ResponseEntity.notFound().build();
    } catch (SecurityException e) {
      log.error("Unauthorized access: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @DeleteMapping("/delete/batch/{batchId}")
  public ResponseEntity<DeleteResponseDto> deleteBatch(
      @PathVariable Long batchId,
      @RequestAttribute("authenticatedUser") User user) {
    
    try {
      DeleteResponseDto response = batchService.deleteBatch(batchId, user);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      log.error("Batch not found: {}", batchId);
      return ResponseEntity.notFound().build();
    } catch (SecurityException e) {
      log.error("Unauthorized access: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

}

