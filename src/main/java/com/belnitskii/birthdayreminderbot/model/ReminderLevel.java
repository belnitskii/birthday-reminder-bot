package com.belnitskii.birthdayreminderbot.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum ReminderLevel {
    HIGH(List.of(60, 30, 14, 7, 3, 1, 0)),
    LOW(List.of(3, 1, 0));

    private final List<Integer> days;

    ReminderLevel(List<Integer> days) {
        this.days = days;
    }
}
