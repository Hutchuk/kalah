package com.hutch.kalah.controller;

import com.hutch.kalah.domain.KalahCreateGameRequest;
import com.hutch.kalah.domain.KalahGameResponse;
import com.hutch.kalah.domain.KalahMoveRequest;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

/**
 * The {@link GameController} exposes rest endpoints that allow games to be created
 * in addition to being viewed.  It also provides an endpoint to process
 * a game move.
 */
@RestController
@RequestMapping("/api/v1/kalah")
public class GameController {

    private GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * This method allows you to create a new game.  {@link KalahCreateGameRequest#playerOneId} will always be
     * the id of starting player.  Both players are required in order to create the game.
     * Will return status 201 on successfully creating the game.
     *
     * @param kalahCreateGameRequest {@link KalahCreateGameRequest}
     * @return {@link ResponseEntity} with the body containing a {@link KalahGameResponse}
     */
    @PostMapping("/create")
    public ResponseEntity<KalahGameResponse> createGame(@RequestBody @Valid KalahCreateGameRequest kalahCreateGameRequest) {
        KalahGameResponse kalahGameResponse = gameService.checkRequestAndCreateGame(kalahCreateGameRequest);
        return new ResponseEntity<>(kalahGameResponse, kalahGameResponse.getStatus());
    }

    /**
     * Tries to find the {@link com.hutch.kalah.entity.KalahGame} with a gameId matching the parameter.
     * Will return status 200 on finding the game and 404 if not found.
     *
     * @param id identifier of the {@link com.hutch.kalah.entity.KalahGame} being searched for
     * @return returns a {@link ResponseEntity} with the body containing a {@link com.hutch.kalah.entity.KalahGame}
     */
    @GetMapping("id/{id}")
    public ResponseEntity<KalahGameResponse> getGame(@PathVariable("id") Long id) {
        KalahGameResponse response = gameService.loadGame(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Processes a request to make a move in the chosen game.
     * Will return status 400 in the following situations:
     * The player does not exist
     * The player is not in the game
     * It isnt the players turn
     * The requested pit is empty
     * The game is over
     * <p>
     * Will return status 200 when the move is completed successfully.
     *
     * @param kalahMoveRequest a {@link KalahMoveRequest}
     * @return returns a {@link ResponseEntity} with the body containing a {@link KalahGameResponse}
     */
    @PostMapping("/move")
    public ResponseEntity<KalahGameResponse> processMove(@RequestBody @Valid KalahMoveRequest kalahMoveRequest) {
        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("playerId/{playerId}")
    public ResponseEntity<List<KalahGame>> findByPlayerId(@PathVariable("playerId") BigInteger playerId) {
        List<KalahGame> games = gameService.findGamesByPlayerId(playerId);
        return new ResponseEntity<>(games, HttpStatus.OK);
    }


}
