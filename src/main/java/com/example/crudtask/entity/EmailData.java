package com.example.crudtask.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Schema(description = "Уникальный идентификатор записи email", required = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Пользователь, к которому принадлежит email", required = true)
    @JsonBackReference
    private User user;

    @Column(name = "email", nullable = false, length = 200)
    @Schema(description = "Адрес электронной почты", required = true, example = "user@example.com")
    private String email;

    public EmailData(User user, String email) {
        this.user = user;
        this.email = email;
    }
}
