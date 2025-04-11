package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final UserService userService;
    private final PersonService personService;

    public NotificationService(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    public List<SendMessage> getDailyNotification() {
        List<SendMessage> sendMessageList = new ArrayList<>();
        List<User> userList = userService.findAll();
        for (User user : userList) {
            Long id = user.getTelegramId();
            if (id != null){
                List<Person> personList = personService.getPersonsToNotifyByReminderLevel(id);
                sendMessageList.add(new SendMessage(id.toString(), formatPersonList(personList)));
            }
        }
        return sendMessageList;
    }

    public String formatPersonList(List<Person> persons) {
        return persons.stream()
                .map(p -> String.format(
                        "👤 %s — %s (%d лет)",
                        p.getName(),
                        p.getBirthdayDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        p.getAge()
                ))
                .collect(Collectors.joining("\n"));
    }
}
