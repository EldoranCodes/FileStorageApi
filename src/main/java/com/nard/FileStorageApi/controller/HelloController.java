package com.nard.FileStorageApi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nard.FileStorageApi.dto.ApiResponse;
import com.nard.FileStorageApi.model.ApiKey;
import com.nard.FileStorageApi.service.ApiKeyService;
import com.nard.FileStorageApi.service.UploadService;

@RestController
public class HelloController {

  private final Logger log = LoggerFactory.getLogger(HelloController.class);

  @Autowired
  private ApiKeyService apiKeyservice;

  @Autowired
  private UploadService uplaodService;

  @GetMapping("/hello")
  public String helloWorld() {
    return "Hello, World";
  }

  @GetMapping("/apikeyTest")
  public ApiResponse apikeyValidationtest(@RequestHeader("x-api-key") String apikey) {

    log.debug("apikey recieved:{}", apikey);

    ApiKey validatedApiKeyConsumer = apiKeyservice.validateApiKey(apikey);
    if (validatedApiKeyConsumer == null) {
      return ApiResponse.error("Api Key is not Valid");
    }
    return ApiResponse.ok("Success validating apikey", validatedApiKeyConsumer);
  }

  @PostMapping("/filenameTest")
  public String valdiateFilename(@RequestParam("filename") String filename) {
    return String.valueOf(uplaodService.validateFilename(filename));
  }
}
