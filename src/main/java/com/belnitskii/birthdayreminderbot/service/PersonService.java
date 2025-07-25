package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.ReminderLevel;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.repository.PersonRepository;
import com.belnitskii.birthdayreminderbot.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    public PersonService(PersonRepository personRepository, UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void savePerson(Person person) {
        validateName(person.getName());
        validateBirthdayDate(person.getBirthdayDate());
        User user = getCurrentUser();
        person.setOwner(user);
        personRepository.save(person);
    }

    @Transactional
    public void savePersonByAdmin(Person person) {
        validateName(person.getName());
        validateBirthdayDate(person.getBirthdayDate());
        personRepository.save(person);
    }

    public Person getPerson(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    @Transactional
    public void updatePerson(Person updatedPerson) {
        validateName(updatedPerson.getName());
        validateBirthdayDate(updatedPerson.getBirthdayDate());
        Person person = personRepository.findById(updatedPerson.getId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с таким ID не найден")
        );
        person.setName(updatedPerson.getName());
        person.setBirthdayDate(updatedPerson.getBirthdayDate());
        person.setReminderLevel(updatedPerson.getReminderLevel());
        personRepository.save(person);
    }

    @Transactional
    public void updatePersonByAdmin(Person updatedPerson) {
        validateName(updatedPerson.getName());
        validateBirthdayDate(updatedPerson.getBirthdayDate());
        Person person = personRepository.findById(updatedPerson.getId()).orElseThrow(
                () -> new IllegalArgumentException("Пользователь с таким ID не найден")
        );
        person.setName(updatedPerson.getName());
        person.setBirthdayDate(updatedPerson.getBirthdayDate());
        person.setReminderLevel(updatedPerson.getReminderLevel());
        person.setOwner(updatedPerson.getOwner());
        personRepository.save(person);
    }

    public List<Person> getAllByAdmin() {
        if (getCurrentUser().isAdmin()){
            return personRepository.findAll();
        } else {
            throw new ForbiddenException();
        }
    }

    public void validateBirthdayDate(LocalDate birthdayDate){
        if (birthdayDate == null){
            throw new IllegalArgumentException("Вы забыли указать день рождения");
        }
        if (birthdayDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем!");
        }
        if (birthdayDate.isBefore(LocalDate.now().minusYears(120))) {
            throw new IllegalArgumentException("Але, а ниче тот факт что люди столько не живут?");
        }
    }

    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Нельзя сохранять пользователя без имени");
        }
    }

    public List<Person> getPersonsByCurrentUser() {
        User user = getCurrentUser();
        return personRepository.findByOwner(user);
    }

    public List<Person> getPersonsToNotifyByReminderLevel(Long telegramId) {
        List<Person> allPersons = personRepository.findByOwner_TelegramId(telegramId);
        List<Person> filteredPersons = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Person person : allPersons) {
            LocalDate nextBirthday = person.getBirthdayDate().withYear(today.getYear());
            if (nextBirthday.isBefore(today)) {
                nextBirthday = nextBirthday.plusYears(1);
            }
            long daysUntilBirthday = ChronoUnit.DAYS.between(today, nextBirthday);
            ReminderLevel level = person.getReminderLevel();
            if (level.getDays().contains((int) daysUntilBirthday)) {
                filteredPersons.add(person);
            }
        }

        filteredPersons.sort(Comparator.comparingLong(p -> {
            LocalDate birthday = p.getBirthdayDate().withYear(today.getYear());
            if (birthday.isBefore(today)) {
                birthday = birthday.plusYears(1);
            }
            return ChronoUnit.DAYS.between(today, birthday);
        }));
        return filteredPersons;
    }

    public List<Person> getPersonsByTelegramId(Long telegramId) {
        return personRepository.findByOwner_TelegramId(telegramId);
    }

    public List<Person> getPersonsByTelegramIdSorted(Long telegramId) {
        return personRepository.findUpcomingBirthdays(telegramId);
    }



    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }

}
