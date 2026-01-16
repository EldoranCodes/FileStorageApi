package com.nard.FileStorageApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nard.FileStorageApi.model.Consumer;

public interface ConsumerRepository extends JpaRepository<Consumer, Long> {

}
