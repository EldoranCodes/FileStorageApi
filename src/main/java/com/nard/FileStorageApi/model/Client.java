package com.nard.FileStorageApi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long clientId;

  private String apiKey;
  private String appName;
  private String adminName;
  private boolean active = true;
}
