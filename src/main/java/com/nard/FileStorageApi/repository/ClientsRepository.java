package com.nard.FileStorageApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.Client;

@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

  Client findByClientId(Long ClientId);

  Client findByApiKey(String apiKey);

}
