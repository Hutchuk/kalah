package com.hutch.kalah.service.impl;

import com.hutch.kalah.domain.KalahCreateGameRequest;
import com.hutch.kalah.domain.KalahGameResponse;
import com.hutch.kalah.domain.KalahMoveRequest;
import com.hutch.kalah.domain.PlaceStonesOutput;
import com.hutch.kalah.domain.constant.KalahMessage;
import com.hutch.kalah.entity.KalahBoard;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.repository.GameRepository;
import com.hutch.kalah.service.GameService;
import com.hutch.kalah.service.PersonService;
import com.hutch.kalah.service.StoneService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * {@inheritDoc}
 */
@Service
public class GameServiceImpl implements GameService {


    GameRepository gameRepository;
    StoneService stoneService;
    PersonService personService;

    public GameServiceImpl(GameRepository gameRepository, StoneService stoneService, PersonService personService) {
        this.gameRepository = gameRepository;
        this.stoneService = stoneService;
        this.personService = personService;
    }


    public KalahGame createGame(Person player1, Person player2) {

        KalahGame kalahGame = new KalahGame();
        kalahGame.setBoard(new KalahBoard());
        kalahGame.setPlayerOne(player1);
        kalahGame.setPlayerTwo(player2);
        kalahGame.setCurrentPlayer(player1);
        kalahGame.initialise(6, 6);

        return kalahGame;
    }

    @Override
    public KalahGame saveGame(KalahGame game) {
        return gameRepository.save(game);
    }

    @Override
    public KalahGameResponse loadGame(long gameId) {

        KalahGameResponse response = new KalahGameResponse();
        Optional<KalahGame> optionalGame = gameRepository.findById(gameId);

        if (optionalGame.isPresent()) {
            response.setKalahGame(optionalGame.get());
            response.setStatus(HttpStatus.OK);
        } else {
            response.setErrorMessage(KalahMessage.GAME_NOT_FOUND.getMessage() + gameId);
            response.setStatus(HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Override
    public boolean checkStartingPitValidity(KalahMoveRequest request, KalahGame game) {
        if (isPlayerOneCurrentMover(game)) {
            return game.getBoard().getPlayerOnePits().get(request.getPit()) > 0;
        } else return game.getBoard().getPlayerTwoPits().get(request.getPit()) > 0;
    }

    /**
     * Processes the requested move.  Validation should be performed via {@link GameService#validateAndProcessMove(KalahMoveRequest)} beforehand.
     *
     * First establishes who the current player is, and then empties the selected pit.
     * While there are stones to be placed it will call {@link StoneService#placeStones}.
     * Each time {@link StoneService#placeStones} is called the boolean indicating which side of the pits
     * are being operated on switches to allow for placing more stones.
     *
     * When there are no more stones to place win conditions are checked along with logic to see if the player has another turn.
     * @param request a valid {@link KalahMoveRequest}
     * @param game    a {@link KalahGame}
     * @return a {@link KalahGameResponse} with the updated game following the moves.
     */
    @Override
    public KalahGameResponse processMove(KalahMoveRequest request, KalahGame game) {
        boolean currentlyProcessingPlayerOnePits = isPlayerOneCurrentMover(game);
        PlaceStonesOutput placeStonesOutput = null;
        int stones;
        int pit = request.getPit();

        KalahGameResponse response = new KalahGameResponse();

        stones = stoneService.getStonesAndEmptyStartingPit(game, currentlyProcessingPlayerOnePits, pit);
        pit++;

        while (stones > 0) {
            placeStonesOutput = stoneService.placeStones(game, pit, currentlyProcessingPlayerOnePits, stones);
            stones = placeStonesOutput.getStones();
            currentlyProcessingPlayerOnePits = !currentlyProcessingPlayerOnePits;
            pit = 1;
        }

        checkForGameOverAndMoveStonesToFinish(game);
        setEndStatusIfApplicable(game, placeStonesOutput, response);

        response.setKalahGame(game);

        return response;
    }

    KalahGame setEndStatusIfApplicable(KalahGame game, PlaceStonesOutput placeStonesOutput, KalahGameResponse response) {
        if (game.isGameOver()) {
            if (game.getBoard().getPlayerOneKalah() > game.getBoard().getPlayerTwoKalah()) {
                game.setWinner(game.getPlayerOne());
                game.getPlayerOne().recordVictory();
                game.getPlayerTwo().recordLoss();
                response.setStatusMessage(String.format("Game over, %s has won", game.getPlayerOne().getName()));
            } else if (game.getBoard().getPlayerTwoKalah() > game.getBoard().getPlayerOneKalah()) {
                game.setWinner(game.getPlayerTwo());
                game.getPlayerTwo().recordVictory();
                game.getPlayerOne().recordLoss();
                response.setStatusMessage(String.format("Game over, %s has won", game.getPlayerTwo().getName()));
            } else {
                response.setStatusMessage("Drawn game");
                game.getPlayerOne().recordDraw();
                game.getPlayerTwo().recordDraw();
            }
        } else {
            if (!placeStonesOutput.isEndedOnKalah()) {
                changePlayerTurn(game);
            } else {
                response.setStatusMessage(KalahMessage.MOVE_AGAIN.getMessage());
            }
        }
        return game;
    }


    KalahGame changePlayerTurn(KalahGame game) {
        if (isPlayerOneCurrentMover(game)) {
            game.setCurrentPlayer(game.getPlayerTwo());
        } else {
            game.setCurrentPlayer(game.getPlayerOne());
        }
        return game;
    }


    KalahGame checkForGameOverAndMoveStonesToFinish(KalahGame game) {

        Integer totalPlayer1 = getPitsTotal(game.getBoard().getPlayerOnePits());
        Integer totalPlayer2 = getPitsTotal(game.getBoard().getPlayerTwoPits());

        if (totalPlayer1.equals(0) || totalPlayer2.equals(0)) {
            game.setGameOver(true);
            stoneService.moveLeftoverStonesToKalah(game, totalPlayer1, totalPlayer2);
        }

        return game;
    }

    int getPitsTotal(Map<Integer, Integer> pits) {
        int total = 0;
        for (int value : pits.values()) {
            total += value;
        }
        return total;
    }

    boolean isPlayerOneCurrentMover(KalahGame game) {
        return game.getCurrentPlayer().getPersonId().equals(game.getPlayerOne().getPersonId());
    }

    @Override
    public KalahGameResponse checkRequestAndCreateGame(KalahCreateGameRequest request) {
        KalahGameResponse response = new KalahGameResponse();
        KalahGame game = null;

        Optional<Person> player1 = personService.findbyId(request.getPlayerOneId());
        Optional<Person> player2 = personService.findbyId(request.getPlayerTwoId());

        if (player1.isPresent()) {
            if (player2.isPresent()) {
                game = createGame(player1.get(), player2.get());
            } else {
                response.setErrorMessage(KalahMessage.PLAYER_NOT_FOUND.getMessage().concat(request.getPlayerTwoId().toString()));
                response.setStatus(HttpStatus.BAD_REQUEST);
            }
        } else {
            response.setErrorMessage(KalahMessage.PLAYER_NOT_FOUND.getMessage().concat(request.getPlayerOneId().toString()));
            response.setStatus(HttpStatus.BAD_REQUEST);
        }

        if (game != null) {
            response.setKalahGame(game);
            saveGame(game);
            response.setStatus(HttpStatus.CREATED);
        }

        return response;
    }

    List<BigInteger> getPlayerIdList(KalahGame kalahGame) {
        List<BigInteger> playerIds = new ArrayList<>();
        playerIds.add(kalahGame.getPlayerOne().getPersonId());
        playerIds.add(kalahGame.getPlayerTwo().getPersonId());

        return playerIds;
    }

    public KalahGameResponse validateAndProcessMove(KalahMoveRequest request) {

        KalahGameResponse response = loadGame(request.getGameId());
        if (response.getStatus().equals(HttpStatus.NOT_FOUND)) {
            response.setStatus(HttpStatus.BAD_REQUEST);
            return response;
        }

        Optional<Person> movingPlayer = personService.findbyId(request.getMovingPlayerId());

        if (!movingPlayer.isPresent()) {
            response.setErrorMessage(KalahMessage.PLAYER_NOT_FOUND.getMessage().concat(request.getMovingPlayerId().toString()));
            response.setStatus(HttpStatus.BAD_REQUEST);
            return response;
        } else if (!getPlayerIdList(response.getKalahGame()).contains(movingPlayer.get().getPersonId())) {
            response.setErrorMessage(KalahMessage.INVALID_PLAYER.getMessage().concat(request.getMovingPlayerId().toString()));
            response.setStatus(HttpStatus.BAD_REQUEST);
            return response;
        }

        KalahGame game = response.getKalahGame();
        if (!game.isGameOver()) {
            if (game.getCurrentPlayer().getPersonId().equals(request.getMovingPlayerId())) {
                if (checkStartingPitValidity(request, game)) {
                    response = processMove(request, game);
                    response.setStatus(HttpStatus.OK);
                } else {
                    response.setErrorMessage(KalahMessage.INVALID_MOVE.getMessage());
                    response.setStatus(HttpStatus.BAD_REQUEST);
                }
            } else {
                response.setErrorMessage(KalahMessage.NOT_PLAYERS_MOVE.getMessage().concat(request.getMovingPlayerId().toString()));
                response.setStatus(HttpStatus.BAD_REQUEST);
            }
        } else {
            response.setErrorMessage(KalahMessage.GAME_OVER_ERROR.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        }

        if (response.getStatus() != HttpStatus.BAD_REQUEST) {
            if (response.getStatusMessage() == null) {
                response.setStatusMessage(KalahMessage.TURN_OVER.getMessage());
            }
            saveGame(game);
        }
        return response;
    }

    @Override
    public List<KalahGame> findGamesByPlayerId(BigInteger playerId) {
        return gameRepository.findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals(playerId, playerId);
    }


}
