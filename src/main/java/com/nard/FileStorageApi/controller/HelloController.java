package com.nard.FileStorageApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nard.FileStorageApi.dto.ApiResponse;
import com.nard.FileStorageApi.service.FilesStorageService;

@RestController
public class HelloController {

  @Autowired
  private FilesStorageService fileService;

  public HelloController(FilesStorageService fileService) {
    this.fileService = fileService;
  }

  @GetMapping("/hello")
  public String helloWorld() {
    return "Hello, World";
  }

  @GetMapping("/filepath")
  public String getBaseFilePath() {
    return fileService.getFilePath();
  }
}
