package com.example.crudtask.service;

import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    User getUserById(Long userId);
    List<User> getAllUsers();

    List<PhoneData> getUserPhones(Long userId);

    List<EmailData> getUserEmails(Long userId);

    Account getUserAccount(Long userId);

    User createUser(User user, BigDecimal initialBalance);

    User updateUser(Long userId, User updatedUser);

    void deleteUser(Long userId);

    void deposit(Long userId, BigDecimal amount);

    void withdraw(Long userId, BigDecimal amount);
}
