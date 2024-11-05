package com.example.crudtask.dao;

import com.example.crudtask.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneDataDAO extends JpaRepository<PhoneData, Long> {

    boolean existsByPhone(String phone);
}
