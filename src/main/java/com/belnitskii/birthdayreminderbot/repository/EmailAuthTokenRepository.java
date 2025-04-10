package com.belnitskii.birthdayreminderbot.repository;

import com.belnitskii.birthdayreminderbot.model.EmailAuthToken;
import com.belnitskii.birthdayreminderbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthTokenRepository extends JpaRepository<EmailAuthToken, Long> {
    Optional<EmailAuthToken> findByToken(String token);

    @Transactional
    @Modifying
    void removeByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmailAuthToken t WHERE t.user = :user")
    void removeByUser(User user);
}
