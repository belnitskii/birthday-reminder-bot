package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("person", new Person());
        return "person-form";
    }

    @GetMapping("/edit/{id}")
    public String editPersonFrom(@PathVariable Long id, Model model){
        Person person = personService.getPerson(id);
        model.addAttribute("person", person);
        return "person-edit";
    }

    @PostMapping("/update")
    public String updatePerson(@ModelAttribute Person person){
        personService.updatePerson(person);
        return "redirect:/person/list";
    }


    @PostMapping("/save")
    public String savePerson(@ModelAttribute Person person) {
        personService.savePerson(person);
        return "redirect:/person/list";
    }

    @PostMapping("/delete")
    public String deletePerson(@RequestParam("id") Long id){
        personService.deletePerson(id);
        return "redirect:/person/list";
    }

    @GetMapping("/list")
    public String showPersonList(Model model) {
        model.addAttribute("people", personService.getAll());
        return "person-list";
    }
}
