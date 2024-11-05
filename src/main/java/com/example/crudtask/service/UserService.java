package com.example.crudtask.service;

import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long userId);
    List<User> getAllUsers();
    List<PhoneData> getUserPhones(Long userId);
    List<EmailData> getUserEmails(Long userId);
    Account getUserAccount(Long userId);
    User updateUser(Long userId, User updatedUser);
    void deleteUser(Long userId);
    void deposit(Long userId, BigDecimal amount);
    void withdraw(Long userId, BigDecimal amount);
    void addEmail(Long userId, String email);
    void updateEmail(Long userId, Long emailId, String newEmail);
    void deleteEmail(Long userId, Long emailId);
    void addPhone(Long userId, String phone);
    void updatePhone(Long userId, Long phoneId, String newPhone);
    void deletePhone(Long userId, Long phoneId);
    Page<User> searchUsers(
            Optional<LocalDate> dateOfBirth,
            Optional<String> phone,
            Optional<String> name,
            Optional<String> email,
            int page,
            int size
    );
}
