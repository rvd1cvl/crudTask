package com.example.crudtask.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@Table(name = "phone_data")
@Schema(description = "Информация о номерах телефонов пользователя")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhoneData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Уникальный идентификатор записи телефона", required = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(name = "Пользователь, к которому принадлежит номер телефона", required = true)
    @JsonBackReference
    private User user;

    @Column(name = "phone", nullable = false, length = 13)
    @Schema(name = "Номер телефона в формате 79207865432", required = true, example = "79207865432")
    private String phone;

    public PhoneData(User user, String phone) {
        this.user = user;
        this.phone = phone;
    }
}
