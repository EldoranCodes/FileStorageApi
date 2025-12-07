package com.nard.FileStorageApi.model;

import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Metadata {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long fileID;

	// * Save files to `./<base-path>/<API_CLIENT_ID>/<Category>/`
	private String uploadedAt; // full filepath

	private int clientId;// <API_CLIENT_ID> // the applciation that will use this api service

	private String category;

	private String fileName; // orignal name of the file
}
