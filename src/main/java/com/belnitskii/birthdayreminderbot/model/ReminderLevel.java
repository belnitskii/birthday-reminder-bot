package com.belnitskii.birthdayreminderbot.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum ReminderLevel {
    HIGH(List.of(60, 30, 14, 7, 3, 1, 0), "Extended reminders (60,30,14,7,3,1,0 days)"),
    LOW(List.of(3, 1, 0), "Basic reminders (3,1,0 days)");

    private final List<Integer> days;
    private final String description;

    ReminderLevel(List<Integer> days, String description) {
        this.days = days;
        this.description = description;
    }
}
