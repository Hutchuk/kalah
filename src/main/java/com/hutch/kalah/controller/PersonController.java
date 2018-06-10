package com.hutch.kalah.controller;

import com.hutch.kalah.entity.Person;
import com.hutch.kalah.service.PersonService;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * The {@link PersonController} sllows for creation and searching of Person entities.
 * This is a very basic implementation with only sufficient functionality to support the
 * running of Kalah games.
 */
@RestController
@RequestMapping(value = "/api/v1/persons", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@ResponseBody
public class PersonController {

    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Creates a new person.  The only required value is {@link Person#name}.
     *
     * @param person see {@link Person}.
     * @return {@link ResponseEntity} with the body containing a {@link Person}
     */
    @PostMapping("/create")
    public ResponseEntity<Person> createPerson(@RequestBody @Valid Person person) {
        Person persistedPerson = personService.createPerson(person);
        persistedPerson.add(linkTo(methodOn(PersonController.class).getPersonById(persistedPerson.getPersonId())).withSelfRel());
        return new ResponseEntity<>(persistedPerson, HttpStatus.CREATED);
    }

    /**
     * Displays all currently saved {@link Person}.
     *
     * @return {@link ResponseEntity} with the body containing a list of {@link Person}
     */
    @GetMapping
    public ResponseEntity<List<Person>> getPersons() {
        List<Person> people = personService.getAllPersons();
        for (Person person : people) {
            person.add(linkTo(methodOn(PersonController.class).getPersonById(person.getPersonId())).withSelfRel());
        }
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    /**
     * Displays a list of all {@link Person}.
     *
     * @param name the {@link Person#name} to be searched for
     * @return {@link ResponseEntity} with the body containing a list of {@link Person}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Person>> getPersonsByName(@PathVariable("name") String name) {
        List<Person> people = personService.findAllByName(name);
        for (Person person : people) {
            person.add(linkTo(methodOn(PersonController.class).getPersonById(person.getPersonId())).withSelfRel());
        }
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    /**
     * Finds and returns the person matching the search id.
     * Will return status 200 if found and 404 if not.
     *
     * @param id the {@link Person#personId} of the {@link Person} being searched for
     * @return {@link ResponseEntity} with the body containing a {@link Person}
     */
    @GetMapping("id/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable("id") BigInteger id) {
        Optional<Person> person = personService.findbyId(id);

        return person.map(person1 -> new ResponseEntity<>(person1, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
