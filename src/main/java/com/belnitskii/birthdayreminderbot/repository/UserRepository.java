package com.belnitskii.birthdayreminderbot.repository;

import com.belnitskii.birthdayreminderbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByTelegramId(Long telegramId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.telegramId = null WHERE u.username = :username")
    void removeTelegramIdByUsername(@Param("username") String userName);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.telegramId = null WHERE u.telegramId = :telegramId")
    void removeTelegramIdByTelegramId(@Param("telegramId") Long telegramId);
}
