package com.nard.FileStorageApi.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stored_file")
@NoArgsConstructor
@Getter
@Setter
public class StoredFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_name")
  private String originalName;

  @Column(unique = true)
  private String uuid;

  @Column(name = "upload_timestamp")
  private LocalDateTime uploadTimestamp;

  @ManyToOne
  @JoinColumn(name = "batch_id")
  private UploadBatch batch;

  @Column(name = "storage_path")
  private String storagePath;

  @PrePersist
  protected void onCreate() {
    if (uuid == null) {
      uuid = UUID.randomUUID().toString();
    }
    if (uploadTimestamp == null) {
      uploadTimestamp = LocalDateTime.now();
    }
  }

}

