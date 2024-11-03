package com.example.crudtask.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Schema(description = "Информация о счете")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор счета", required = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Пользователь, к которому принадлежит счет", required = true)
    private User user;

    @Column(name = "balance", precision = 19, scale = 2)
    @Schema(description = "Баланс счета в рублях и копейках", example = "1000.50")
    private BigDecimal balance;
}