package com.management.people.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.people.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
