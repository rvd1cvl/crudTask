package com.example.crudtask.dao;

import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    @Query("SELECT p FROM PhoneData p WHERE p.user.id = :userId")
    List<PhoneData> findUserPhones(Long userId);

    @Query("SELECT e FROM EmailData e WHERE e.user.id = :userId")
    List<EmailData> findUserEmails(Long userId);
}
