package com.hutch.kalah.repository;

import com.hutch.kalah.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, BigInteger> {
    List<Person> findAllByNameEqualsIgnoreCase(String name);
}
