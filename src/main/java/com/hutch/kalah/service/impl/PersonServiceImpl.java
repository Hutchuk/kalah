package com.hutch.kalah.service.impl;

import com.hutch.kalah.entity.Person;
import com.hutch.kalah.repository.PersonRepository;
import com.hutch.kalah.service.PersonService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public List<Person> getAllPersons() {
        return personRepository.findAll();

    }

    @Override
    public List<Person> findAllByName(String name) {
        return personRepository.findAllByNameEqualsIgnoreCase(name);
    }

    @Override
    public Optional<Person> findbyId(BigInteger id) {
        return personRepository.findById(id);
    }

}
