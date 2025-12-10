package com.nard.FileStorageApi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nard.FileStorageApi.model.Client;
import com.nard.FileStorageApi.repository.ClientsRepository;

@SpringBootTest
public class ClientsRepositoryTests {

  @Autowired
  private ClientsRepository clientRository;

  @Test
  public void ClientRepositoryFunctionsTest() {
    Client testClient = new Client();

    testClient.setApiKey("123zxc");
    testClient.setClientId(null);

    Client savedClient = clientRository.save(testClient);

    // Client savedClient = testClient;

    assert savedClient.getClientId() != null;

    // test#2
    Client founByApiKeyClient = clientRository.findByApiKey(savedClient.getApiKey());
    assert founByApiKeyClient != null;
    assert founByApiKeyClient.getApiKey().equals(savedClient.getApiKey());

    // cleaning
    clientRository.delete(savedClient);

  }
}
