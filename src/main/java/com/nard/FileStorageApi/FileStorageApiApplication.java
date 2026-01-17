package com.nard.FileStorageApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class FileStorageApiApplication {

  private final Logger log = LoggerFactory.getLogger(FileStorageApiApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(FileStorageApiApplication.class, args);
  }

  // make a seed data like demo api key ownded by demo_owner
  // must seed or create hte base file storage base file path
}
