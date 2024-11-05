package com.example.crudtask.service.impl;

import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.EmailDataDAO;
import com.example.crudtask.dao.PhoneDataDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final EmailDataDAO emailDataDAO;
    private final PhoneDataDAO phoneDataDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccountDAO accountDAO, EmailDataDAO emailDataDAO, PhoneDataDAO phoneDataDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.emailDataDAO = emailDataDAO;
        this.phoneDataDAO = phoneDataDAO;
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
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        existingUser.setName(updatedUser.getName());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setPassword(updatedUser.getPassword());

        return userDAO.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
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

    @Override
    @Transactional
    public void addEmail(Long userId, String email) {
        if (emailDataDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Этот email уже используется другим пользователем");
        }
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        EmailData emailData = new EmailData(user, email);
        emailDataDAO.save(emailData);
    }

    @Override
    @Transactional
    public void updateEmail(Long userId, Long emailId, String newEmail) {
        if (emailDataDAO.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Этот email уже используется другим пользователем");
        }
        EmailData emailData = emailDataDAO.findById(emailId)
                .orElseThrow(() -> new IllegalArgumentException("Email не найден"));

        if (!emailData.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя изменить email другого пользователя");
        }

        emailData.setEmail(newEmail);
        emailDataDAO.save(emailData);
    }

    @Override
    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        EmailData emailData = emailDataDAO.findById(emailId)
                .orElseThrow(() -> new IllegalArgumentException("Email не найден"));

        if (!emailData.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя удалить email другого пользователя");
        }

        emailDataDAO.delete(emailData);
    }

    @Override
    @Transactional
    public void addPhone(Long userId, String phone) {
        if (phoneDataDAO.existsByPhone(phone)) {
            throw new IllegalArgumentException("Этот номер телефона уже используется другим пользователем");
        }
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        PhoneData phoneData = new PhoneData(user, phone);
        phoneDataDAO.save(phoneData);
    }

    @Override
    @Transactional
    public void updatePhone(Long userId, Long phoneId, String newPhone) {
        if (phoneDataDAO.existsByPhone(newPhone)) {
            throw new IllegalArgumentException("Этот номер телефона уже используется другим пользователем");
        }
        PhoneData phoneData = phoneDataDAO.findById(phoneId)
                .orElseThrow(() -> new IllegalArgumentException("Номер телефона не найден"));

        if (!phoneData.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя изменить номер телефона другого пользователя");
        }

        phoneData.setPhone(newPhone);
        phoneDataDAO.save(phoneData);
    }

    @Override
    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData phoneData = phoneDataDAO.findById(phoneId)
                .orElseThrow(() -> new IllegalArgumentException("Номер телефона не найден"));

        if (!phoneData.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя удалить номер телефона другого пользователя");
        }

        phoneDataDAO.delete(phoneData);
    }

    @Override
    public Page<User> searchUsers(Optional<LocalDate> dateOfBirth, Optional<String> phone, Optional<String> name, Optional<String> email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return userDAO.findUsersByFilters(dateOfBirth, phone, name, email, pageable);
    }
}