package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.service.TelegramAuthTokenService;
import com.belnitskii.birthdayreminderbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;
    private final TelegramAuthTokenService authTokenService;

    @Autowired
    public AuthController(UserService userService, TelegramAuthTokenService authTokenService) {
        this.userService = userService;
        this.authTokenService = authTokenService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/account")
    public String showAccount(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "account";
    }

    @GetMapping("/token")
    public String showTokenPage() {
        return "token";
    }

    @PostMapping("/auth/telegram/token")
    public String generateTelegramToken(Principal principal, Model model){
        String userName = principal.getName();
        model.addAttribute("token", authTokenService.generateTokenForUser(userName));
        return "token";
    }

    @PostMapping("/auth/telegram/remove")
    public String removeTelegramToken(Principal principal){
        String userName = principal.getName();
        authTokenService.removeToken(userName);
        return "redirect:/account";
    }
}