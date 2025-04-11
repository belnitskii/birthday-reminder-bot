package com.belnitskii.birthdayreminderbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@EnableScheduling
@SpringBootApplication
public class BirthdayReminderBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(BirthdayReminderBotApplication.class, args);
	}
}
