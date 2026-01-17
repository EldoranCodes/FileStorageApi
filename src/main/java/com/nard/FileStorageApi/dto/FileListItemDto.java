package com.nard.FileStorageApi.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileListItemDto {

  private Long id;
  private String originalName;
  private String storedName;
  private String contentType;
  private Long fileSize;
  private LocalDateTime createdAt;

  // URL template. Consumer backend supplies apiKey when calling the API.
  private String streamUrl;

}
