package com.nard.FileStorageApi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.StoredFile;
import com.nard.FileStorageApi.model.UploadBatch;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
  
  List<StoredFile> findByBatch(UploadBatch batch);
  
  List<StoredFile> findByBatchId(Long batchId);

}

