package com.belnitskii.birthdayreminderbot.repository;

import com.belnitskii.birthdayreminderbot.model.Person;
import com.belnitskii.birthdayreminderbot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByOwner(User owner);

    List<Person> findByOwner_TelegramId(Long ownerTelegramId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Person t WHERE t.owner.id = :owner_id")
    void removeAllByOwner_Id(@Param("owner_id") Long ownerId);
}
