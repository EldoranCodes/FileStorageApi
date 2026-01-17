package com.nard.FileStorageApi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nard.FileStorageApi.model.FileMetaData;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {

  boolean existsByStoredName(String storedName);

  Optional<FileMetaData> findByStoredNameAndOwnerAndDeletedAtIsNull(String storedName, Long owner);

  List<FileMetaData> findByOwnerAndDeletedAtIsNull(Long owner);

  List<FileMetaData> findByOwnerAndAppNameAndDeletedAtIsNull(Long owner, String appName);

  List<FileMetaData> findByDeletedAtIsNotNull();

}
