package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.ReminderLevel;
import com.belnitskii.birthdayreminderbot.model.Role;
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

    @GetMapping("/list-persons")
    public String getAll(Model model) {
        model.addAttribute("people", personService.getAllByAdmin());
        return "admin/admin-list-persons";
    }

    @GetMapping("/list-users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAllByAdmin());
        return "admin/admin-list-users";
    }


    @PostMapping("/delete")
    public String deletePerson(@RequestParam("id") Long id){
        personService.deletePerson(id);
        return "redirect:/admin/list-persons";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam("id") Long id){
        userService.deleteUserByAdmin(id);
        return "redirect:/admin/list-users";
    }

    @GetMapping("/edit/{id}")
    public String editPersonForm(@PathVariable Long id, Model model){
        Person person = personService.getPerson(id);
        List<User> users = userService.findAllByAdmin();
        model.addAttribute("person", person);
        model.addAttribute("users", users);
        model.addAttribute("allReminderLevels", ReminderLevel.values());
        return "admin/admin-edit";
    }

    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("roles", Role.values());
        return "admin/admin-edit-user";
    }

    @PostMapping("/update")
    public String updatePerson(@ModelAttribute Person person, @RequestParam Long ownerId){
        User owner = userService.findById(ownerId);
        person.setOwner(owner);
        personService.updatePersonByAdmin(person);
        return "redirect:/admin/list-persons";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUserByAdmin(user);
        return "redirect:/admin/list-users";
    }


    @PostMapping("/save")
    public String savePersonByAdmin(@RequestParam String name,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdayDate,
                                    @RequestParam ReminderLevel reminderLevel,
                                    @RequestParam Long ownerId) {
        User owner = userService.findById(ownerId);
        Person person = new Person();
        person.setName(name);
        person.setBirthdayDate(birthdayDate);
        person.setOwner(owner);
        person.setReminderLevel(reminderLevel);
        personService.savePersonByAdmin(person);
        return "redirect:/admin/list-persons";
    }

    @PostMapping("/save-user")
    public String saveUser(@RequestParam("name") String name,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("role") String role) {
        userService.createUserByAdmin(name, email, password, role);
        return "redirect:/admin/list-users";
    }

    @GetMapping("/admin-form")
    public String showAdminForm(Model model) {
        model.addAttribute("users", userService.findAllByAdmin());
        model.addAttribute("person", new Person());
        model.addAttribute("allReminderLevels", ReminderLevel.values());
        return "admin/admin-form";
    }

    @GetMapping("/admin-form-user")
    public String showAdminFormUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/admin-form-user";
    }

    @GetMapping("/account")
    public String showAdminAccount(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "admin/admin-account";
    }
}
