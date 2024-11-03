package com.example.crudtask.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "user_table")
@Schema(description = "Информация о пользователе")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя", required = true)
    private BigInteger id;

    @Column(name = "name", nullable = false, length = 500)
    @Schema(description = "Имя пользователя", required = true, example = "Иван Иванов")
    private String name;

    @Column(name = "date_of_birth")
    @Schema(description = "Дата рождения пользователя", example = "01.05.1993")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "password", nullable = false, length = 500)
    @Schema(description = "Пароль пользователя", required = true, example = "password123")
    private String password;
}
