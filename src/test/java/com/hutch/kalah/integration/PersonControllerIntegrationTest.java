package com.hutch.kalah.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Test
    public void personController_givenPersonSaved_shouldReturnStatus201() throws Exception {

        Person dave = new Person("Dave");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/persons/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dave)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value(is(dave.getName())));
    }

    @Test
    public void personControllerFindById_givenPersonExists_shouldReturnPerson() throws Exception {

        Person dave = new Person("Dave");

        personService.createPerson(dave);

        mockMvc.perform(get("/api/v1/persons/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE));
    }
}
