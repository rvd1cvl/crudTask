package com.example.crudtask.dao;

import com.example.crudtask.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDAO extends JpaRepository<Account, Long> {
    Account findByUserId(Long userId);
}