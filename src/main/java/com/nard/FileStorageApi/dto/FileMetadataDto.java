package com.nard.FileStorageApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadataDto {
  
  private Long fileId;
  private String originalName;
  private String path;
  private String uuid;
  private String uploadTimestamp;

}

