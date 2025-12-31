package com.nard.FileStorageApi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.UploadBatch;

@Repository
public interface UploadBatchRepository extends JpaRepository<UploadBatch, Long> {
}

