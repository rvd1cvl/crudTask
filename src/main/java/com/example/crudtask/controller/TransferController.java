package com.example.crudtask.controller;

import com.example.crudtask.config.JwtUtil;
import com.example.crudtask.service.impl.TransferService;
import com.example.crudtask.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public String transferMoney(
            @Parameter(description = "ID получателя перевода", required = true) @RequestParam Long transferToId,
            @Parameter(description = "Сумма перевода", required = true) @RequestParam BigDecimal value) {

        Long transferFromId = userService.getCurrentUserId();

        return transferService.transferMoney(transferFromId, transferToId, value);
    }
}