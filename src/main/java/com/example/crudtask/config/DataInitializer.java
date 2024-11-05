package com.example.crudtask.config;

import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.dao.UserDAO;
import com.example.crudtask.service.impl.UserServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.dao.UserDAO;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer {

    @Autowired
    private UserDAO userDAO;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        // Вызываем инициализацию только один раз
        if (!initialized) {
            initializeUsers();
            initialized = true;
        }
    }

    @Transactional
    public void initializeUsers() {
        // Проверяем, есть ли пользователи
        if (userDAO.count() > 0) {
            System.out.println("Пользователи уже существуют, инициализация не требуется.");
            return;
        }

        // Создаем пользователей, если они отсутствуют
        createUser("Иван Иванов", LocalDate.of(1993, 5, 1), "password123", "79207865432");
        createUser("Петр Петров", LocalDate.of(1990, 8, 15), "password456", "79207865433");
        createUser("Светлана Светлова", LocalDate.of(1988, 3, 20), "password789", "79207865434");
    }

    private void createUser(String name, LocalDate dateOfBirth, String password, String phoneNumber) {
        User user = new User();
        user.setName(name);
        user.setDateOfBirth(java.sql.Date.valueOf(dateOfBirth));
        user.setPassword(password);

        // Создаем и добавляем телефонный номер
        PhoneData phoneData = new PhoneData(user, phoneNumber);
        user.addPhone(phoneData);

        // Валидация пользователя
        user.validate();

        userDAO.save(user);
        System.out.println("Пользователь " + name + " был создан.");
    }
}
