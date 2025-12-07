package com.nard.FileStorageApi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class client {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int clientId;

	private String appName;

	private String apiKey;
	private boolean active = true;
}
