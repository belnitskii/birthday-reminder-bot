package com.belnitskii.birthdayreminderbot.repository;

import com.belnitskii.birthdayreminderbot.model.TelegramAuthToken;
import com.belnitskii.birthdayreminderbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TelegramAuthTokenRepository extends JpaRepository<TelegramAuthToken, Long> {
    Optional<TelegramAuthToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM TelegramAuthToken t WHERE t.user = :user")
    void removeByUser(@Param("user") User user);
}
