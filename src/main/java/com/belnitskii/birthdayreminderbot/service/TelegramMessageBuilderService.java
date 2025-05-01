package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelegramMessageBuilderService {

    private final PersonService personService;
    private final UserService userService;


    public TelegramMessageBuilderService(PersonService personService, UserService userService) {
        this.personService = personService;
        this.userService = userService;
    }

    public String buildBirthdayListMessage(Long telegramUserId) {
        User user = userService.getCurrentUserByTelegramId(telegramUserId);
        if (user != null) {
            List<Person> people = personService.getPersonsByTelegramIdSorted(telegramUserId);
            if (people.isEmpty()) {
                return "You have no records.";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<pre>");
            sb.append(String.format("%-15s %-12s %-8s\n", "Name", "Date", "Days left"));
            sb.append("-----------------------------------\n");
            for (Person person : people) {
                String name = person.getName();
                String date = person.getBirthdayDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                long days = ChronoUnit.DAYS.between(LocalDate.now(), person.getBirthdayDate().withYear(LocalDate.now().getYear()));
                if (days < 0) {
                    days = (long) (LocalDate.now().lengthOfYear() - LocalDate.now().getDayOfYear()) + person.getBirthdayDate().getDayOfYear();
                }
                sb.append(String.format("%-15s %-12s %-8s\n", name, date, days));
            }
            sb.append("</pre>");
            return sb.toString();
        }
        return "User not found, please link your account first";
    }

    public String buildBirthdayNextMessage(Long telegramUserId) {
        User user = userService.getCurrentUserByTelegramId(telegramUserId);
        if (user != null) {
            List<Person> people = personService.getPersonsByTelegramIdSorted(telegramUserId);
            if (people.isEmpty()) {
                return "You have no records.";
            }
            StringBuilder sb = new StringBuilder();
            people.stream()
                    .limit(3)
                    .forEach(person -> {
                        String name = person.getName();
                        String date = person.getBirthdayDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                        long days = ChronoUnit.DAYS.between(LocalDate.now(), person.getBirthdayDate().withYear(LocalDate.now().getYear()));
                        if (days < 0) {
                            days = LocalDate.now().lengthOfYear() - LocalDate.now().getDayOfYear() + person.getBirthdayDate().getDayOfYear();
                        }

                        sb.append("🎂 *").append(name).append("*\n");
                        sb.append("📅 Date: `").append(date).append("`\n");
                        sb.append("⏳ Days left: *").append(days).append("* days.\n\n");
                    });
            return sb.toString();
        }
        return "User not found, please link your account first";
    }
}
