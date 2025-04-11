package com.belnitskii.birthdayreminderbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Table(name = "person")
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false)
    private LocalDate birthdayDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'LOW'")
    private ReminderLevel reminderLevel = ReminderLevel.LOW;

    public int getAge() {
        return Period.between(birthdayDate, LocalDate.now()).getYears();
    }
}
