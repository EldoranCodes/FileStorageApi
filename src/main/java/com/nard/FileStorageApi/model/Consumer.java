package com.nard.FileStorageApi.model;

import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Setter
@Getter
@AllArgsConstructor
public class Consumer {
  // devs that uses the api
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name; //
  private String status;
  private LocalDateTime createdAt;
  private String role;

  public Consumer(String name, String status, String role) {
    this.createdAt = LocalDateTime.now();
    this.name = name;
    this.status = status;
    this.role = role;
  }

  @PrePersist
  protected void onCreate() {
    if (createdAt == null)
      createdAt = LocalDateTime.now();
    if (status == null)
      status = "active";
    if (role == null)
      role = "consumer";
  }

  @Override
  public String toString() {
    return "Consumer [id=" + id + ", name=" + name + ", status=" + status + ", createdAt=" + createdAt + ", role="
        + role + "]";
  }

}
