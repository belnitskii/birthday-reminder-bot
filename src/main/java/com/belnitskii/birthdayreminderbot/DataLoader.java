package com.belnitskii.birthdayreminderbot;

import com.belnitskii.birthdayreminderbot.model.Role;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@mail.ru");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN.name());
            admin.setEnabled(true);
            userRepository.save(admin);
        }
        if(userRepository.findByEmail("user@mail.ru").isEmpty()){
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@mail.ru");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(Role.USER.name());
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
