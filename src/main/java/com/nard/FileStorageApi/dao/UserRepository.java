package com.nard.FileStorageApi.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByName(String name);

  Optional<User> findByApiKey(String hashedApiKey);

}
