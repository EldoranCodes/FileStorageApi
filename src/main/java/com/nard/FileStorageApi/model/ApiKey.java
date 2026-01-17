package com.nard.FileStorageApi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Setter
@Getter
@AllArgsConstructor
public class ApiKey {
  // devs that uses the api
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String apiKey;
  private String appName; // for owners applciation
  private Long owner; // applciation name

  private LocalDateTime createdAt;
  private String status;

  public ApiKey(String apiKey, String appName, Long owner) {
    this.apiKey = apiKey;
    this.appName = appName;
    this.owner = owner;

    this.createdAt = LocalDateTime.now();
    this.status = "active";
  }

  @Override
  public String toString() {
    return "ApiKey [id=" + id + ", apiKey=" + apiKey + ", appName=" + appName + ", owner=" + owner + ", createdAt="
        + createdAt + ", status=" + status + "]";
  }

}
