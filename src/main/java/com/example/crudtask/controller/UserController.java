package com.example.crudtask.controller;

import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Получить пользователя по ID", description = "Получить информацию о пользователе по его уникальному идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "ID пользователя для получения информации") @PathVariable Long userId) {
        User user = userService.getUserById(userId).get();
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Получить номера телефонов пользователя", description = "Получить список номеров телефонов пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номера телефонов найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/phones")
    public ResponseEntity<List<PhoneData>> getUserPhones(
            @Parameter(description = "ID пользователя для получения номеров телефонов") @PathVariable Long userId) {
        List<PhoneData> phones = userService.getUserPhones(userId);
        return ResponseEntity.ok(phones);
    }

    @Operation(summary = "Получить адреса электронной почты пользователя", description = "Получить список адресов электронной почты пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Адреса электронной почты найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/emails")
    public ResponseEntity<List<EmailData>> getUserEmails(
            @Parameter(description = "ID пользователя для получения адресов электронной почты") @PathVariable Long userId) {
        List<EmailData> emails = userService.getUserEmails(userId);
        return ResponseEntity.ok(emails);
    }

    @Operation(summary = "Получить информацию о счете пользователя", description = "Получить информацию о счете пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о счете найдена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/account")
    public ResponseEntity<Account> getUserAccount(
            @Parameter(description = "ID пользователя для получения информации о счете") @PathVariable Long userId) {
        Account account = userService.getUserAccount(userId);
        return ResponseEntity.ok(account);
    }


    @Operation(summary = "Получить всех пользователей", description = "Получить список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей найден"),
            @ApiResponse(responseCode = "204", description = "Пользователи не найдены")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
