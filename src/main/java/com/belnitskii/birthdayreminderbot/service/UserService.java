package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.EmailAuthToken;
import com.belnitskii.birthdayreminderbot.model.Role;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.repository.EmailAuthTokenRepository;
import com.belnitskii.birthdayreminderbot.repository.PersonRepository;
import com.belnitskii.birthdayreminderbot.repository.TelegramAuthTokenRepository;
import com.belnitskii.birthdayreminderbot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final TelegramAuthTokenRepository telegramAuthTokenRepository;
    private final EmailAuthTokenRepository emailAuthTokenRepository;
    private final EmailAuthTokenService emailAuthTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PersonRepository personRepository, TelegramAuthTokenRepository telegramAuthTokenRepository, EmailAuthTokenRepository emailAuthTokenRepository, EmailAuthTokenService emailAuthTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.telegramAuthTokenRepository = telegramAuthTokenRepository;
        this.emailAuthTokenRepository = emailAuthTokenRepository;
        this.emailAuthTokenService = emailAuthTokenService;
    }

    @Transactional
    public User registerUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());
        User userToSave;

        if (existingUserOpt.isPresent()){
            User existingUser = existingUserOpt.get();
            if (existingUser.isEnabled()){
                throw new IllegalArgumentException("User with this email already exists and is active.");
            } else {
                existingUser.setUsername(user.getUsername());
                existingUser.setEmail(user.getEmail());
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                userToSave = existingUser;
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userToSave = user;
        }
        User savedUser = userRepository.save(userToSave);
        try {
            emailAuthTokenService.sendTokenToEmail(savedUser.getEmail(), savedUser);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return savedUser;
    }

    public List<User> findAllByAdmin() {
        return userRepository.findAll();
    }

    @Transactional
    public void createUserByAdmin(String name, String email, String password, String role) {
        if (getCurrentUser().isAdmin()) {
            User user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            if (Role.fromString(role).isPresent()) {
                user.setRole(role);
            }
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    @Transactional
    public void updateUserByAdmin(User user) {
        if (getCurrentUser().isAdmin()) {
            User dbUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            dbUser.setUsername(user.getUsername());
            dbUser.setEmail(user.getEmail());
            if (!Objects.equals(user.getPassword(), "")) {
                dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            dbUser.setRole(user.getRole());
            dbUser.setEnabled(true);
            userRepository.save(dbUser);
        }
    }

    public User findById(Long ownerId) {
        return userRepository.findById(ownerId).orElseThrow();
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user found");
    }

    public User getCurrentUserByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteUserByAdmin(Long id) {
        if (getCurrentUser().isAdmin()) {
            User userToDelete = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            personRepository.removeAllByOwner_Id(id);
            telegramAuthTokenRepository.removeByUser(userToDelete);
            emailAuthTokenRepository.removeByUser(userToDelete);
            userRepository.deleteById(id);
        }
    }
}
