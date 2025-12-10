package com.nard.FileStorageApi.model;

import java.nio.file.Path;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Metadata {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long fileID;

  private Long clientId; // api consumers client id
  private String originalFileName; // orignal name of the file
  private String storagePath;// * Save files to `./<base-path>/<API_CLIENT_ID>/<appName>/<appName>/filename`
  private LocalDateTime uploadedAt = LocalDateTime.now(); // date and time
  private String uploadedBy; // clients/consumers uploader or their application's user that uploaded the file

}
