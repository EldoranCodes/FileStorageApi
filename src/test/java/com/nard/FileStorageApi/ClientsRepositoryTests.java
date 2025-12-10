package com.nard.FileStorageApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.repository.ClientsRepository;

@SpringBootTest
public class ClientsRepositoryTests {

  @Autowired
  private ClientsRepository clientRepository;

  @Test
  public void ClientRepositoryFunctionsTest() {
    Client testClient = clientRepository.findByClientId(2L);

    assertNotNull(testClient, "Client with ID 2 should exist");

    assertEquals(2, testClient.getClientId());
    assertNotNull(testClient.getClientName());

  }
}
