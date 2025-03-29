package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/person")
public class PersonApiController {


    private final PersonService personService;

    @Autowired
    public PersonApiController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatePerson(@RequestBody Person person){
        Map<String, String> errors = new HashMap<>();
        try {
            personService.validateName(person.getName());
        } catch (IllegalArgumentException e) {
            errors.put("nameError", e.getMessage());
        }
        try {
            personService.validateBirthdayDate(person.getBirthdayDate());
        } catch (IllegalArgumentException e) {
            errors.put("dateError", e.getMessage());
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok().build();
    }
}
