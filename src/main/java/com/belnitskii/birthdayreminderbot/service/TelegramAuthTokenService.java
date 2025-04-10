package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.TelegramAuthToken;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.repository.TelegramAuthTokenRepository;
import com.belnitskii.birthdayreminderbot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramAuthTokenService {

    TelegramAuthTokenRepository tokenRepository;
    UserRepository userRepository;

    public TelegramAuthTokenService(TelegramAuthTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public String generateTokenForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        tokenRepository.removeByUser(user);
        TelegramAuthToken authToken = new TelegramAuthToken();
        authToken.setUser(user);
        authToken.setToken(UUID.randomUUID().toString());
        authToken.setCreatedAt(LocalDateTime.now());
        tokenRepository.save(authToken);
        return authToken.getToken();
    }

    public void removeTokenForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        tokenRepository.removeByUser(user);
        userRepository.removeTelegramIdByEmail(user.getEmail());
    }

    public Optional<User> connectTelegram(String token, Long telegramId) {
        if (userRepository.findByTelegramId(telegramId).isPresent()) {
            userRepository.removeTelegramIdByTelegramId(telegramId);
        }
        return tokenRepository.findByToken(token)
                .map(authToken -> {
                    User user = authToken.getUser();
                    user.setTelegramId(telegramId);
                    userRepository.save(user);
                    tokenRepository.delete(authToken);
                    return user;
                });
    }
}
