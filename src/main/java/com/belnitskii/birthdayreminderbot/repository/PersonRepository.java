package com.belnitskii.birthdayreminderbot.repository;

import com.belnitskii.birthdayreminderbot.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
