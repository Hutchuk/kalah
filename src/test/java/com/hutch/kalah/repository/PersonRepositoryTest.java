package com.hutch.kalah.repository;

import com.hutch.kalah.entity.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findAllByNameEqualsIgnoreCase_existingPerson_shouldFindPerson() {
        // Given
        Person dave = new Person("dave");
        testEntityManager.persist(dave);
        testEntityManager.flush();

        // When
        List<Person> people = personRepository.findAllByNameEqualsIgnoreCase(dave.getName());

        // Then
        assertThat(people).contains(dave);
    }


}