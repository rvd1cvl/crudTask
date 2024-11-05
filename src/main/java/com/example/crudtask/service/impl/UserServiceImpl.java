package com.example.crudtask.service.impl;

import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;


    public UserServiceImpl(UserDAO userDAO, AccountDAO accountDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userDAO.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public List<PhoneData> getUserPhones(Long userId) {
        return userDAO.findUserPhones(userId);
    }

    @Override
    public List<EmailData> getUserEmails(Long userId) {
        return userDAO.findUserEmails(userId);
    }

    @Override
    public Account getUserAccount(Long userId) {
        return accountDAO.findByUserId(userId);
    }

    @Override
    @Transactional
    public User createUser(User user, BigDecimal initialBalance) {
        User savedUser = userDAO.save(user);

        Account account = new Account();
        account.setUser(savedUser);
        account.setBalance(initialBalance);

        accountDAO.save(account);
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userDAO.findById(userId).get();
        if (existingUser == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        existingUser.setName(updatedUser.getName());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setPassword(updatedUser.getPassword());
        return userDAO.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userDAO.findById(userId).get();
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        userDAO.delete(user);
    }

    @Override
    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        Account account = accountDAO.findByUserId(userId);
        account.setBalance(account.getBalance().add(amount));
        accountDAO.save(account);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, BigDecimal amount) {
        Account account = accountDAO.findByUserId(userId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Недостаточно средств на счете");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountDAO.save(account);
    }
}