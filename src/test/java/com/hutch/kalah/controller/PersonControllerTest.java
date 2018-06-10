package com.hutch.kalah.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonService mockPersonService;

    @Test
    public void createPerson_givenPerson_shouldReturnStatusCreated() throws Exception {

        Person dave = new Person("Dave");
        Person daveSaved = new Person("Dave");
        daveSaved.setPersonId(BigInteger.valueOf(1));


        when(mockPersonService.createPerson(any(Person.class))).thenReturn(daveSaved);
        mockMvc.perform(post("/api/v1/persons/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dave)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.name").value(is(daveSaved.getName())))
                .andExpect(jsonPath("$.personId").value(is(daveSaved.getPersonId().intValue())));

        verify(mockPersonService, times(1)).createPerson(any(Person.class));
    }

    @Test
    public void getPersons_givenPersonsExist_shouldReturnListOfPersons() throws Exception {
        Person pete = new Person("Pete");
        Person kate = new Person("Kate");

        when(mockPersonService.getAllPersons()).thenReturn(Arrays.asList(pete, kate));

        mockMvc.perform(get("/api/v1/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(pete.getName())))
                .andExpect(jsonPath("$[1].name", is(kate.getName())));

        verify(mockPersonService, times(1)).getAllPersons();
    }

    @Test
    public void getPersonsByName_givenTwoPersonsWithSameName_shouldReturnBothPersons() throws Exception {
        Person pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));
        Person pete2 = new Person("Pete");
        pete2.setPersonId(BigInteger.valueOf(5));

        when(mockPersonService.findAllByName("pete")).thenReturn(Arrays.asList(pete, pete2));

        mockMvc.perform(get("/api/v1/persons/name/pete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].personId", is(pete.getPersonId().intValue())))
                .andExpect(jsonPath("$[1].personId", is(pete2.getPersonId().intValue())));

        verify(mockPersonService, times(1)).findAllByName("pete");

    }

    @Test
    public void getPersonById_givenPersonExists_shouldReturnPerson() throws Exception {
        Person pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));

        Optional<Person> optionalPete = Optional.of(pete);

        when(mockPersonService.findbyId(BigInteger.valueOf(1))).thenReturn(optionalPete);

        mockMvc.perform(get("/api/v1/persons/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
                .andExpect(jsonPath("$.personId", is(pete.getPersonId().intValue())))
                .andExpect(jsonPath("$.name", is(pete.getName())));

        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(1));
    }

    @Test
    public void getPersonById_givenPersonNotExists_shouldReturn404() throws Exception {
        Optional<Person> optional = Optional.empty();

        when(mockPersonService.findbyId(BigInteger.valueOf(1))).thenReturn(optional);

        mockMvc.perform(get("/api/v1/persons/id/1"))
                .andExpect(status().isNotFound());

        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(1));

    }
}