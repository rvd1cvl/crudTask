package com.example.crudtask.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "email_data")
@Schema(description = "Информация о адресах электронной почты пользователя")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор записи email", required = true, example = "1")
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Пользователь, к которому принадлежит email", required = true, example = "1")
    @JsonBackReference
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    @Schema(description = "Адрес электронной почты", required = true, example = "ivanov@mail.com")
    private String email;

    public EmailData(User user, String email) {
        this.user = user;
        this.email = email;
    }
}
