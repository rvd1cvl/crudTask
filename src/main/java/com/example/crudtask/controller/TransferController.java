package com.example.crudtask.controller;

import com.example.crudtask.config.JwtUtil;
import com.example.crudtask.service.impl.TransferService;
import com.example.crudtask.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "Перевести деньги от одного пользователя другому",
            description = "Этот метод позволяет перевести деньги от текущего пользователя к другому пользователю.",
            tags = {"Transfer"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция перевода успешно выполнена", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Неверные параметры перевода", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> transferMoney(@RequestParam Long fromId, @RequestParam Long toId,
                                           @RequestParam BigDecimal value, @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Authorization token is missing or invalid");
            }

            // Извлекаем токен и проверяем его
            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);

            if (username == null || !jwtUtil.validateToken(jwtToken, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }

            // Вызов сервиса для перевода денег
            String result = transferService.transferMoney(fromId, toId, value);
            return ResponseEntity.ok(result);  // Вернем успешный ответ с кодом 200
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // Вернем ошибку с кодом 400, если есть проблема с валидацией
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());  // Ошибка сервера
        }
    }
}