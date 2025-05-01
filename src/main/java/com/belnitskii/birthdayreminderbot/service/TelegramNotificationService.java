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
public class TelegramNotificationService {

    private final UserService userService;
    private final PersonService personService;

    public TelegramNotificationService(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    public List<SendMessage> getDailyNotification() {
        List<SendMessage> sendMessageList = new ArrayList<>();
        List<User> userList = userService.findAllByAdmin();
        for (User user : userList) {
            Long id = user.getTelegramId();
            if (id != null){
                List<Person> personList = personService.getPersonsToNotifyByReminderLevel(id);
                sendMessageList.add(new SendMessage(Long.toString(id), formatPersonList(personList)));
            }
        }
        return sendMessageList;
    }

    public String formatPersonList(List<Person> persons) {

        StringBuilder sb = new StringBuilder();

        persons.stream()
                .limit(3)
                .forEach(person -> {
                    String name = person.getName();
                    String date = person.getBirthdayDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    long days = ChronoUnit.DAYS.between(
                            LocalDate.now(),
                            person.getBirthdayDate().withYear(LocalDate.now().getYear())
                    );

                    if (days < 0) {
                        days = LocalDate.now().lengthOfYear() - LocalDate.now().getDayOfYear()
                                + person.getBirthdayDate().getDayOfYear();
                    }

                    sb.append("🎂 *").append(name).append("*\n");
                    sb.append("📅 Date: `").append(date).append("`\n");
                    sb.append("⏳ Days left: *").append(days).append("* days.\n\n");
                });

        return sb.toString();
    }
}
