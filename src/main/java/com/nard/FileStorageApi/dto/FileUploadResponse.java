package com.nard.FileStorageApi.dto;

import java.util.List;
import com.nard.FileStorageApi.model.Metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FileUploadResponse {
  private boolean success;
  private String message;
  private List<SavedFilesDTO> savedFiles;

}
