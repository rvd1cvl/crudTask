package com.example.crudtask.config;

import com.example.crudtask.dao.AccountDAO;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DataInitializer {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private SecurityConfig securityConfig;

    public static String currentUserName = JwtUtil.getCurrentUsername();

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

        createUser("Иван Иванов", LocalDate.of(1993, 5, 1), "password123",
                Set.of("79207865432"), Set.of("ivanov@mail.com"), BigDecimal.valueOf(1000.0));

        createUser("Петр Петров", LocalDate.of(1990, 8, 15), "password456",
                Set.of("79207865433"), Set.of("petrov@mail.com"), BigDecimal.valueOf(1500.0));

        createUser("Светлана Светлова", LocalDate.of(1988, 3, 20), "password789",
                Set.of("79207865434"), Set.of("svetlova@mail.com"), BigDecimal.valueOf(2000.0));
    }

    private void createUser(String name, LocalDate dateOfBirth, String password,
                            Set<String> phoneNumbers, Set<String> emails, BigDecimal initialBalance) {

        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setName(name);
        user.setDateOfBirth(java.sql.Date.valueOf(dateOfBirth));
        user.setPassword(encodedPassword);
        user.setUserEmail(new EmailData(user, emails.iterator().next()).getEmail());

        userDAO.save(user);

        Account account = new Account(initialBalance);
        account.setUser(user);

        accountDAO.save(account);

        phoneNumbers.forEach(phoneNumber -> {
            PhoneData phoneData = new PhoneData(user, phoneNumber);
            user.addPhone(phoneData);
        });

        emails.forEach(email -> {
            EmailData emailData = new EmailData(user, email);
            user.addEmail(emailData);
        });

        user.validate();
        userDAO.save(user);
    }
}
