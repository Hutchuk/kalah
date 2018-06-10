package com.hutch.kalah.service;

import com.hutch.kalah.entity.Person;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Convenience interface to allow for more logic to be added around {@link Person} in future.
 */
public interface PersonService {
    Person createPerson(Person person);
    List<Person> getAllPersons();
    List<Person> findAllByName(String name);
    Optional<Person> findbyId(BigInteger id);
}
