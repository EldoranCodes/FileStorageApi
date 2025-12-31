package com.nard.FileStorageApi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name = "users")
@Setter
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name; // application name, e.g., "demo-app"

  private String status; // ACTIVE / INACTIVE

  @ManyToOne
  @JoinColumn(name = "accounts_id")
  private Account account;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "api_key")
  private String apiKey; // hashed API key

}
