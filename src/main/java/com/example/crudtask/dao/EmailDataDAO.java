package com.example.crudtask.dao;

import com.example.crudtask.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailDataDAO extends JpaRepository<EmailData, Long> {
    List<EmailData> findByUserId(Long userId);
}
