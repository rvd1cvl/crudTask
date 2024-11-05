package com.example.crudtask.config;

import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private UserDAO userDAO;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        if (!initialized) {
            initializeUsers();
            initialized = true;
        }
    }

    @Transactional
    public void initializeUsers() {
        if (userDAO.count() > 0) {
            return;
        }

        createUser("Иван Иванов", LocalDate.of(1993, 5, 1), "password123", Arrays.asList("79207865432"), Arrays.asList("ivanov@mail.com"), BigDecimal.valueOf(1000.0));
        createUser("Петр Петров", LocalDate.of(1990, 8, 15), "password456", Arrays.asList("79207865433"), Arrays.asList("petrov@mail.com"), BigDecimal.valueOf(1500.0));
        createUser("Светлана Светлова", LocalDate.of(1988, 3, 20), "password789", Arrays.asList("79207865434"), Arrays.asList("svetlova@mail.com"), BigDecimal.valueOf(2000.0));
    }

    private void createUser(String name, LocalDate dateOfBirth, String password, List<String> phoneNumbers, List<String> emails, BigDecimal initialBalance) {
        User user = new User();
        user.setName(name);
        user.setDateOfBirth(java.sql.Date.valueOf(dateOfBirth));
        user.setPassword(password);

        // Create Account with initial balance and set bidirectional relationship
        Account account = new Account(initialBalance);
        account.setUser(user);  // Sets the user in account, and account in user.

        for (String phoneNumber : phoneNumbers) {
            PhoneData phoneData = new PhoneData(user, phoneNumber);
            user.addPhone(phoneData);
        }

        for (String email : emails) {
            EmailData emailData = new EmailData(user, email);
            user.addEmail(emailData);
        }

        user.validate();

        userDAO.save(user);
    }

}
