package com.hutch.kalah.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutch.kalah.domain.KalahCreateGameRequest;
import com.hutch.kalah.domain.KalahMoveRequest;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.fielddescriptor.KalahCreateGameRequestDescription;
import com.hutch.kalah.fielddescriptor.KalahGameDescription;
import com.hutch.kalah.fielddescriptor.KalahGameResponseDescription;
import com.hutch.kalah.fielddescriptor.KalahMoveRequestDescription;
import com.hutch.kalah.repository.GameRepository;
import com.hutch.kalah.repository.PersonRepository;
import com.hutch.kalah.testutil.TestGameUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class GameControllerIntegrationTest {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    GameRepository gameRepository;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createGame_givenTwoValidPlayers_shouldReturnStatus201() throws Exception {

        Person pete = new Person("Pete");
        Person dave = new Person("Dave");

        pete = personRepository.save(pete);
        dave = personRepository.save(dave);

        KalahCreateGameRequest kalahCreateGameRequest = new KalahCreateGameRequest();
        kalahCreateGameRequest.setPlayerOneId(pete.getPersonId());
        kalahCreateGameRequest.setPlayerTwoID(dave.getPersonId());


        mockMvc.perform(post("/api/v1/kalah/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahCreateGameRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame.playerOne.name").value(is(pete.getName())))
                .andExpect(jsonPath("$.kalahGame.playerTwo.name").value(is(dave.getName())))
                .andDo(document("createGame/Success",
                        requestFields(KalahCreateGameRequestDescription.KalahCreateGameRequest()),
                        responseFields(KalahGameResponseDescription.KalahGameResponse())));
    }

    @Test
    public void createGame_givenMissingPlayer_shouldReturnStatus400() throws Exception {

        Person pete = new Person("Pete");
        Person dave = new Person("Dave");

        personRepository.save(pete);
        dave.setPersonId(BigInteger.valueOf(99));


        KalahCreateGameRequest kalahCreateGameRequest = new KalahCreateGameRequest();
        kalahCreateGameRequest.setPlayerOneId(pete.getPersonId());
        kalahCreateGameRequest.setPlayerTwoID(dave.getPersonId());

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/v1/kalah/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahCreateGameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame").doesNotExist())
                .andDo(document("createGame/Failure",
                        requestFields(KalahCreateGameRequestDescription.KalahCreateGameRequest()),
                        responseFields(KalahGameResponseDescription.KalahGameResponse())));
    }

    @Test
    public void getGame_givenGameExists_shouldReturnGame() throws Exception {

        Person pete = new Person("Pete");
        Person dave = new Person("Dave");
        KalahGame game = TestGameUtil.createGame(pete, dave);
        gameRepository.save(game);

        mockMvc.perform(get("/api/v1/kalah/id/" + game.getGameId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame.playerOne.name").value(is(pete.getName())))
                .andExpect(jsonPath("$.kalahGame.playerTwo.name").value(is(dave.getName())))
                .andDo(document("getGame/Success",
                        responseFields(KalahGameResponseDescription.KalahGameResponse())));
    }

    @Test
    public void getGame_givenGameDoesntExist_shouldReturn404() throws Exception {

        mockMvc.perform(get("/api/v1/kalah/id/222"))
                .andExpect(status().isNotFound())
                .andDo(document("getGame/Failure"));
    }

    @Test
    public void processMove_givenGameExists_shouldMakeMove() throws Exception {

        Person pete = new Person("Pete");
        Person dave = new Person("Dave");
        KalahGame game = TestGameUtil.createGame(pete, dave);
        gameRepository.save(game);

        KalahMoveRequest kalahMoveRequest = new KalahMoveRequest();
        kalahMoveRequest.setGameId(game.getGameId());
        kalahMoveRequest.setMovingPlayerId(game.getCurrentPlayer().getPersonId());
        kalahMoveRequest.setPit(1);

        mockMvc.perform(post("/api/v1/kalah/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahMoveRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.kalahGame.playerOne.name").value(is(pete.getName())))
                .andExpect(jsonPath("$.kalahGame.playerTwo.name").value(is(dave.getName())))
                .andExpect(jsonPath("$.kalahGame.currentPlayer.name").value(is(pete.getName())))
                .andExpect(jsonPath("$.kalahGame.board.playerOnePits.1").value(is(0)))
                .andDo(document("processMove/Success",
                        requestFields(KalahMoveRequestDescription.KalahMoveRequest()),
                        responseFields(KalahGameResponseDescription.KalahGameResponse())));
    }

    @Test
    public void processMove_givenGameNotExists_shouldReturnError() throws Exception {

        KalahMoveRequest kalahMoveRequest = new KalahMoveRequest();
        kalahMoveRequest.setGameId(25);
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(1));
        kalahMoveRequest.setPit(1);

        mockMvc.perform(post("/api/v1/kalah/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(kalahMoveRequest)))
                .andExpect(status().isBadRequest())
                .andDo(document("processMove/Failure"));

    }

    @Test
    public void findByPlayerId_givenPlayerInGames_shouldReturnGames() throws Exception {

        Person pete = new Person("Pete");
        Person dave = new Person("Dave");
        Person kate = new Person("Kate");
        KalahGame game1 = TestGameUtil.createGame(pete, dave);
        KalahGame game2 = TestGameUtil.createGame(pete, kate);
        gameRepository.saveAll(Arrays.asList(game1, game2));


        mockMvc.perform(get("/api/v1/kalah/playerId/" + pete.getPersonId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[0].playerOne.name").value(is(pete.getName())))
                .andExpect(jsonPath("$.[1].playerOne.name").value(is(pete.getName())))
                .andDo(document("getByPlayerId/Success",
                        responseFields(KalahGameDescription.KalahGameList())));
    }
}
