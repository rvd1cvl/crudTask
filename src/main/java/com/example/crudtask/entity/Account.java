package com.example.crudtask.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
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
    @Schema(description = "Уникальный идентификатор счета", required = true, example = "1001")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Пользователь, к которому принадлежит счет", required = true, example = "1")
    @JsonBackReference
    private User user;

    @Column(name = "balance", precision = 19, scale = 2)
    @Schema(description = "Баланс счета в рублях и копейках", example = "1000.50")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal balance;

    @Column(name = "initial_balance")
    @Schema(description = "Баланс счета в рублях и копейках", example = "1000.50")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal initialBalance;

    public Account(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
        this.balance = initialBalance;
    }

    public void setUser(User user) {
        this.user = user;
        if (user.getAccount() != this) {
            user.setAccount(this);
        }
    }
}