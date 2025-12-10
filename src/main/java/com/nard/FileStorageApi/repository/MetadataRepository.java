package com.nard.FileStorageApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.Metadata;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, Long> {

}
