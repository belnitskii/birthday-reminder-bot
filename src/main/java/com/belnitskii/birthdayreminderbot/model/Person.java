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
    private String name;
    private LocalDate birthdayDate;

    public int getAge() {
        return Period.between(birthdayDate, LocalDate.now()).getYears();
    }
}
