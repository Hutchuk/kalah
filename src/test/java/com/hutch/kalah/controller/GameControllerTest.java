package com.hutch.kalah.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutch.kalah.domain.KalahCreateGameRequest;
import com.hutch.kalah.domain.KalahGameResponse;
import com.hutch.kalah.domain.KalahMoveRequest;
import com.hutch.kalah.entity.KalahBoard;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.service.GameService;
import com.hutch.kalah.testutil.TestGameUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    Person dave;
    Person pete;
    KalahCreateGameRequest kalahCreateGameRequest;
    KalahGameResponse kalahGameResponse;
    KalahGame kalahGame;
    KalahMoveRequest kalahMoveRequest;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService mockGameService;

    @Before
    public void setUp() {
        pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));
        dave = new Person("Dave");
        dave.setPersonId(BigInteger.valueOf(2));

        kalahCreateGameRequest = new KalahCreateGameRequest();
        kalahCreateGameRequest.setPlayerOneId(pete.getPersonId());
        kalahCreateGameRequest.setPlayerTwoID(dave.getPersonId());

        kalahGame = TestGameUtil.createGame(pete, dave);
        kalahGameResponse = new KalahGameResponse();
        kalahGameResponse.setKalahGame(kalahGame);

        kalahMoveRequest = new KalahMoveRequest();
        kalahMoveRequest.setPit(1);
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(1));
        kalahMoveRequest.setGameId(1);
    }

    @Test
    public void createGame() throws Exception {

        kalahGameResponse.setStatus(HttpStatus.CREATED);
        when(mockGameService.checkRequestAndCreateGame(any(KalahCreateGameRequest.class))).thenReturn(kalahGameResponse);

        mockMvc.perform(post("/api/v1/kalah/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahCreateGameRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame").exists());

        verify(mockGameService, times(1)).checkRequestAndCreateGame(any(KalahCreateGameRequest.class));
    }

    @Test
    public void getGame() throws Exception {

        kalahGame.setGameId(1);
        kalahGameResponse.setStatus(HttpStatus.OK);

        when(mockGameService.loadGame(anyLong())).thenReturn(kalahGameResponse);

        mockMvc.perform(get("/api/v1/kalah/id/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame").exists());

        verify(mockGameService, times(1)).loadGame(anyLong());
    }

    @Test
    public void processMove() throws Exception {

        kalahGameResponse.setStatus(HttpStatus.OK);
        when(mockGameService.validateAndProcessMove(any(KalahMoveRequest.class))).thenReturn(kalahGameResponse);

        mockMvc.perform(post("/api/v1/kalah/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahMoveRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame").exists());


        verify(mockGameService, times(1)).validateAndProcessMove(any(KalahMoveRequest.class));
    }

}