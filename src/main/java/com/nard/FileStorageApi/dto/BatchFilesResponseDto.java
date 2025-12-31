package com.nard.FileStorageApi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchFilesResponseDto {
  
  private Long batchId;
  private String status;
  private List<FileMetadataDto> files;

}

