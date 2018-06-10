package com.hutch.kalah.service.impl;

import com.hutch.kalah.entity.Person;
import com.hutch.kalah.repository.PersonRepository;
import com.hutch.kalah.service.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceImplTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository mockPersonRepository;

    @Before
    public void setUp() {
        Person dave = new Person("Dave");
        dave.setPersonId(BigInteger.valueOf(2));
        Person pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));
        Person kate = new Person("Kate");
        Optional<Person> optionalDave = Optional.of(dave);

        when(mockPersonRepository.findById(dave.getPersonId())).thenReturn(optionalDave);
        when(mockPersonRepository.save(any(Person.class))).thenReturn(pete);
        when(mockPersonRepository.findAll()).thenReturn(Arrays.asList(dave, pete, kate));
        when(mockPersonRepository.findAllByNameEqualsIgnoreCase(pete.getName())).thenReturn(Collections.singletonList(pete));
    }

    @Test
    public void findById_givenExistingPerson_shouldFindPerson() {
        // Given
        BigInteger id = BigInteger.valueOf(2);
        String dave = "Dave";

        // When
        Optional<Person> found = personService.findbyId(id);

        // Then
        assertThat(found.get().getName()).isEqualToIgnoringCase(dave);
        verify(mockPersonRepository, times(1)).findById(id);
    }

    @Test
    public void createPerson_givenValidDetails_shouldCreatePerson() {
        // Given
        BigInteger id = BigInteger.valueOf(1);
        Person pete = new Person("Pete");

        // When
        Person foundPete = personService.createPerson(pete);

        // Then
        assertThat(foundPete.getPersonId()).isEqualTo(id);
        verify(mockPersonRepository, times(1)).save(pete);
    }

    @Test
    public void getAllPersons_givenPersonsExist_shouldReturnListOfPersons() {
        // Given
        List<String> peopleToFind = Arrays.asList("Dave", "Pete", "Kate");

        // When
        List<Person> foundPersonsList = personService.getAllPersons();

        // Then
        assertThat(foundPersonsList).extracting("name").containsAll(peopleToFind);
        verify(mockPersonRepository, times(1)).findAll();
    }

    @Test
    public void findAllByName_givenExistingName_shouldReturnFoundPersons() {
        // Given
        String pete = "Pete";

        // When
        List<Person> foundPersonsList = personService.findAllByName(pete);

        // Then
        assertThat(foundPersonsList).extracting("name").containsOnly(pete);
        verify(mockPersonRepository, times(1)).findAllByNameEqualsIgnoreCase(pete);
    }
}