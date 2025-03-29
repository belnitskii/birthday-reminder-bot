package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public void savePerson(Person person) {
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
        validateBirthdayDate(updatedPerson.getBirthdayDate());
        Person person = personRepository.findById(updatedPerson.getId()).orElse(null);
        if (person != null) {
            person.setName(updatedPerson.getName());
            person.setBirthdayDate(updatedPerson.getBirthdayDate());
            personRepository.save(person);
        }
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    private void validateBirthdayDate(LocalDate birthdayDate){
        if (birthdayDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем!");
        }
        if (birthdayDate.isBefore(LocalDate.now().minusYears(120))) {
            throw new IllegalArgumentException("Але, а ниче тот факт что люди столько не живут?");
        }
    }

}
