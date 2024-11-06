package com.example.crudtask.controller;

import com.example.crudtask.config.JwtUtil;
import com.example.crudtask.entity.Account;
import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import com.example.crudtask.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
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
        User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
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


    @Operation(summary = "Обновить информацию о пользователе", description = "Обновить информацию о существующем пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Удалить пользователя", description = "Удалить пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Пополнить баланс пользователя", description = "Пополнить баланс пользователя на определенную сумму")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно пополнен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        userService.deposit(userId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Снять средства с баланса пользователя", description = "Снять определенную сумму с баланса пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Средства успешно сняты"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        userService.withdraw(userId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Добавить email пользователю", description = "Добавить новый email к существующему пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email добавлен"),
            @ApiResponse(responseCode = "400", description = "Email уже используется"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/{userId}/emails")
    public ResponseEntity<Void> addEmail(@PathVariable Long userId, @RequestParam String email) {
        userService.addEmail(userId, email);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить email пользователя", description = "Обновить существующий email пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email обновлен"),
            @ApiResponse(responseCode = "400", description = "Email уже используется"),
            @ApiResponse(responseCode = "404", description = "Email не найден или доступ запрещен")
    })
    @PutMapping("/{userId}/emails/{emailId}")
    public ResponseEntity<Void> updateEmail(@PathVariable Long userId, @PathVariable Long emailId, @RequestParam String newEmail) {
        userService.updateEmail(userId, emailId, newEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить email пользователя", description = "Удалить email из списка пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Email удален"),
            @ApiResponse(responseCode = "404", description = "Email не найден или доступ запрещен")
    })
    @DeleteMapping("/{userId}/emails/{emailId}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long userId, @PathVariable Long emailId) {
        userService.deleteEmail(userId, emailId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Добавить номер телефона пользователю", description = "Добавить новый номер телефона к существующему пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номер телефона добавлен"),
            @ApiResponse(responseCode = "400", description = "Номер уже используется"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/{userId}/phones")
    public ResponseEntity<Void> addPhone(@PathVariable Long userId, @RequestParam String phone) {
        userService.addPhone(userId, phone);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Обновить номер телефона пользователя", description = "Обновить существующий номер телефона пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Номер телефона обновлен"),
            @ApiResponse(responseCode = "400", description = "Номер уже используется"),
            @ApiResponse(responseCode = "404", description = "Номер телефона не найден или доступ запрещен")
    })
    @PutMapping("/{userId}/phones/{phoneId}")
    public ResponseEntity<Void> updatePhone(@PathVariable Long userId, @PathVariable Long phoneId, @RequestParam String newPhone) {
        userService.updatePhone(userId, phoneId, newPhone);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить номер телефона пользователя", description = "Удалить номер телефона пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Номер телефона удален"),
            @ApiResponse(responseCode = "404", description = "Номер телефона не найден или доступ запрещен")
    })
    @DeleteMapping("/{userId}/phones/{phoneId}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long userId, @PathVariable Long phoneId) {
        userService.deletePhone(userId, phoneId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск пользователей с фильтрами и пагинацией", description = "Поиск пользователей с различными критериями фильтрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей найден"),
            @ApiResponse(responseCode = "204", description = "Пользователи не найдены")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam(value = "dateOfBirth", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<User> users = userService.searchUsers(
                Optional.ofNullable(dateOfBirth),
                Optional.ofNullable(phone),
                Optional.ofNullable(name),
                Optional.ofNullable(email),
                page,
                size
        );

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
