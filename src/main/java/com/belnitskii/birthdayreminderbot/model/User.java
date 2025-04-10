package com.belnitskii.birthdayreminderbot.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role = Role.USER.name();

    @Column(unique = true)
    private Long telegramId;

    private boolean enabled = false;

    public boolean isAdmin() {
        return getRole().equals(Role.ADMIN.name());
    }

    public boolean isTelegramRegistered(){
        return getTelegramId() != null;
    }
}
