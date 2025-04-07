package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.service.PersonService;
import com.belnitskii.birthdayreminderbot.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PersonService personService;
    private final UserService userService;

    public AdminController(PersonService personService, UserService userService) {
        this.personService = personService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String getAll(Model model) {
        model.addAttribute("people", personService.getAll());
        return "admin-list";
    }


    @PostMapping("/delete")
    public String deletePerson(@RequestParam("id") Long id){
        personService.deletePerson(id);
        return "redirect:/admin/list";
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable Long id, Model model){
        Person person = personService.getPerson(id);
        List<User> users = userService.findAll();
        model.addAttribute("person", person);
        model.addAttribute("users", users);
        return "admin-edit";
    }

    @PostMapping("/update")
    public String updatePerson(@ModelAttribute Person person, @RequestParam Long ownerId){
        User owner = userService.findById(ownerId);
        person.setOwner(owner);
        personService.updatePersonByAdmin(person);
        return "redirect:/admin/list";
    }


    @PostMapping("/save")
    public String savePersonByAdmin(@RequestParam String name,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayDate,
                                    @RequestParam Long ownerId) {
        User owner = userService.findById(ownerId);
        Person person = new Person();
        person.setName(name);
        person.setBirthdayDate(birthdayDate);
        person.setOwner(owner);
        personService.savePersonByAdmin(person);
        return "redirect:/admin/list";
    }

    @GetMapping("/admin-form")
    public String showAdminForm(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("person", new Person());
        return "admin-form";
    }
}
