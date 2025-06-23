package com.belnitskii.birthdayreminderbot.service;

import com.belnitskii.birthdayreminderbot.model.EmailAuthToken;
import com.belnitskii.birthdayreminderbot.model.User;
import com.belnitskii.birthdayreminderbot.repository.EmailAuthTokenRepository;
import com.belnitskii.birthdayreminderbot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailAuthTokenService {
    private static final Logger logger = LoggerFactory.getLogger(EmailAuthTokenService.class);
    private final JavaMailSender mailSender;
    private EmailAuthTokenRepository tokenRepository;
    private UserRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl;


    public EmailAuthTokenService(JavaMailSender mailSender, EmailAuthTokenRepository tokenRepository, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    public void sendTokenToEmail(String email, User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String token = generateTokenForUser(user);
        String link = baseUrl + "/verify-email?token=" + token;

        String htmlMsg = "<h3>Hello!</h3>"
                + "<p>To complete registration, click the link below:</p>"
                + "<a href=\"" + link + "\">Confirm Email</a>"
                + "<p>Or paste this in your browser:<br>" + link + "</p>";

        helper.setFrom("belnitskii.aleksandr@gmail.com");
        helper.setTo(email);
        helper.setSubject("WELCOME");
        helper.setText(htmlMsg, true);  // true = HTML

        logger.info("Sending HTML email to {}", email);
        mailSender.send(message);
    }

    public String generateTokenForUser(User user) {
        User dbUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        tokenRepository.removeByUser(dbUser);

        EmailAuthToken authToken = new EmailAuthToken();
        authToken.setToken(UUID.randomUUID().toString());
        authToken.setUser(dbUser);
        authToken.setCreatedAt(LocalDateTime.now());
        tokenRepository.save(authToken);
        return authToken.getToken();
    }

    public Optional<EmailAuthToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void remove(String token) {
        logger.info("Removing token: {}", token);
        tokenRepository.removeByToken(token);
    }
}
