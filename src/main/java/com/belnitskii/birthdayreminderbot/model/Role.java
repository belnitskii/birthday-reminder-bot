package com.belnitskii.birthdayreminderbot.model;

import java.util.Optional;

public enum Role {
    ADMIN,
    USER;

    public static Optional<Role> fromString(String roleName) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return Optional.of(role);
            }
        }
        return Optional.empty();
    }
}
