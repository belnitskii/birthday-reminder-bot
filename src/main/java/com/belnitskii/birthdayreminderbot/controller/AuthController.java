package com.belnitskii.birthdayreminderbot.controller;

import com.belnitskii.birthdayreminderbot.model.EmailAuthToken;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.service.EmailAuthTokenService;
import com.belnitskii.birthdayreminderbot.service.TelegramAuthTokenService;
import com.belnitskii.birthdayreminderbot.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserService userService;
    private final TelegramAuthTokenService authTokenService;
    private final EmailAuthTokenService emailAuthTokenService;

    @Autowired
    public AuthController(UserService userService, TelegramAuthTokenService authTokenService, EmailAuthTokenService emailAuthTokenService) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.emailAuthTokenService = emailAuthTokenService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @SneakyThrows
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        emailAuthTokenService.sendTokenToEmail(user.getEmail(), user);
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
        String email = principal.getName();
        model.addAttribute("token", authTokenService.generateTokenForUser(email));
        return "token";
    }

    @PostMapping("/auth/telegram/remove")
    public String removeTelegramToken(Principal principal){
        authTokenService.removeTokenForUser(principal.getName());
        return "redirect:/account";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        EmailAuthToken authToken = emailAuthTokenService.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));
        User user = authToken.getUser();
        user.setEnabled(true);
        userService.saveUser(user);
        emailAuthTokenService.remove(authToken.getToken());
        return "redirect:/login?verified";
    }
}