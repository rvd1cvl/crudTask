package com.example.crudtask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Entity
@Table(name = "user_table")
@Schema(description = "Информация о пользователе")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя", required = true)
    private Long id;

    @Column(name = "name", length = 500)
    @Schema(description = "Имя пользователя", required = true, example = "Иван Иванов")
    private String name;

    @Column(name = "date_of_birth")
    @Schema(description = "Дата рождения пользователя", example = "01.05.1993")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column(name = "password", length = 500)
    @Schema(description = "Пароль пользователя", required = true, example = "password123")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Schema(
            description = "Список номеров телефонов пользователя",
            example = "[{\"id\": 1, \"phone\": \"79207865432\"}, {\"id\": 2, \"phone\": \"79876543210\"}]"
    )
    @JsonManagedReference
    private Set<PhoneData> phones = new HashSet<>();

    @Schema(
            description = "Список email-адресов пользователя",
            example = "[{\"id\": 1, \"email\": \"ivanov@mail.com\"}]"
    )
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<EmailData> emails = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    @Schema(description = "Аккаунт пользователя",
            example = "{\"balance\": 1000.50, \"user\": {\"id\": 1, \"name\": \"Иван Иванов\"}}")
    @JsonManagedReference
    private Account account;

    @Column(unique = true, nullable = false)
    private String userEmail;

    public User(String email, String password) {
        this.userEmail = email;
        this.password = password;
    }

    public void addEmail(EmailData emailData) {
        emails.add(emailData);
        emailData.setUser(this);
    }

    public void addPhone(PhoneData phoneData) {
        phones.add(phoneData);
        phoneData.setUser(this);
    }

    public User(Set<EmailData> emails) {
        if (emails != null && !emails.isEmpty()) {
            this.userEmail = emails.iterator().next().getEmail();
        }
        this.emails = emails;
    }

    public void setEmails(Set<EmailData> emails) {
        this.emails = emails;

        if (!emails.isEmpty() && userEmail == null) {
            this.userEmail = emails.iterator().next().getEmail();
        }
    }

    public void validate() {
        if (phones.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен иметь хотя бы один номер телефона.");
        }
        if (emails.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен иметь хотя бы один email.");
        }
        if (account == null) {
            throw new IllegalArgumentException("Пользователь должен иметь аккаунт.");
        }
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Transactional
    public String getUsername() {
        Hibernate.initialize(this.emails);
        return emails.isEmpty() ? null : emails.iterator().next().getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
