package com.example.crudtask.service.impl;

import com.example.crudtask.config.JwtUtil;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final EmailDataDAO emailDataDAO;
    private final PhoneDataDAO phoneDataDAO;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccountDAO accountDAO, EmailDataDAO emailDataDAO, PhoneDataDAO phoneDataDAO, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.emailDataDAO = emailDataDAO;
        this.phoneDataDAO = phoneDataDAO;
        this.jwtUtil = jwtUtil;
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

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            String username = authentication.getName();
            User currentUser = userDAO.findByEmail(username);
            return currentUser != null ? currentUser.getId() : null;
        }

        return null;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }


        currentUser.setName(updatedUser.getName());
        currentUser.setDateOfBirth(updatedUser.getDateOfBirth());
        currentUser.setPassword(updatedUser.getPassword());

        updatePhones(currentUser, updatedUser);
        updateEmails(currentUser, updatedUser);
        updateAccount(currentUser, updatedUser);

        return userDAO.save(currentUser);
    }

    private void updatePhones(User currentUser, User updatedUser) {
        for (PhoneData updatedPhone : updatedUser.getPhones()) {
            boolean phoneExists = currentUser.getPhones().stream()
                    .anyMatch(existingPhone -> existingPhone.getPhone().equals(updatedPhone.getPhone()));

            if (!phoneExists) {
                updatedPhone.setUser(currentUser);
                currentUser.getPhones().add(updatedPhone);
            }
        }

        currentUser.getPhones().removeIf(existingPhone -> !updatedUser.getPhones().contains(existingPhone));
    }

    private void updateEmails(User currentUser, User updatedUser) {
        Set<String> existingEmails = new HashSet<>();
        for (EmailData emailData : currentUser.getEmails()) {
            existingEmails.add(emailData.getEmail());
        }

        Set<String> updatedEmails = new HashSet<>();
        for (EmailData emailData : updatedUser.getEmails()) {
            updatedEmails.add(emailData.getEmail());
        }

        if (!existingEmails.equals(updatedEmails)) {
            currentUser.getEmails().removeIf(emailData -> !updatedEmails.contains(emailData.getEmail()));

            for (EmailData updatedEmailData : updatedUser.getEmails()) {
                boolean emailExists = existingEmails.contains(updatedEmailData.getEmail());
                if (!emailExists) {
                    updatedEmailData.setUser(currentUser);
                    currentUser.getEmails().add(updatedEmailData);
                }
            }
        }
    }

    private void updateAccount(User currentUser, User updatedUser) {
        if (updatedUser.getAccount() != null) {
            Account existingAccount = currentUser.getAccount();
            if (existingAccount != null) {
                existingAccount.setBalance(updatedUser.getAccount().getBalance());
            } else {
                Account newAccount = updatedUser.getAccount();
                newAccount.setUser(currentUser);
                currentUser.setAccount(newAccount);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        userDAO.delete(user);
    }

    @Override
    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
        Account account = accountDAO.findByUserId(userId);
        account.setBalance(account.getBalance().add(amount));
        accountDAO.save(account);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, BigDecimal amount) {
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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
        User currentUser = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!JwtUtil.getCurrentUsername().equals(currentUser.getUsername())) {
            throw new IllegalArgumentException("Может изменяться только текущий пользователь");
        }
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