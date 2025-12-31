package com.nard.FileStorageApi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nard.FileStorageApi.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}

