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
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long fileID;

  private String originalFileName; // orignal name of the file
  private Path storagePath;// * Save files to `./<base-path>/<API_CLIENT_ID>/<appName>/<appName>/filename`
  private LocalDateTime uploadedAt = LocalDateTime.now(); // date and time
  private String uploadedBy; // can be user name or tin or id

}
