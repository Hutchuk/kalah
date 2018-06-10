package com.hutch.kalah.service;

import com.hutch.kalah.domain.KalahCreateGameRequest;
import com.hutch.kalah.domain.KalahGameResponse;
import com.hutch.kalah.domain.KalahMoveRequest;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;

import java.math.BigInteger;
import java.util.List;

/**
 * Service interface providing methods to handle the logic of a Kalah game.
 * The following describes how the board is laid out:
 * <p>
 * Player two pits      6  5  4  3  2  1
 * Player two Kalah- 0                    0 -Player 1 Kalah
 * Player one pits      1  2  3  4  5  6
 */
public interface GameService {
    /**
     * @param player1 a {@link Person} representing the first player in the game.  This player always goes first
     * @param player2 a {@link Person} representing the second player in the game.
     * @return returns a newly created and initialised {@link KalahGame}.
     */
    KalahGame createGame(Person player1, Person player2);

    /**
     * Saves the {@link KalahGame} passed to it.
     *
     * @param game a {@link KalahGame}
     * @return the saved {@link KalahGame} containing an {@link KalahGame#gameId} if not present before.
     */
    KalahGame saveGame(KalahGame game);

    /**
     * Attempts to retrieve the game with matching {@link KalahGame#gameId}.
     * If not found then {@link KalahGameResponse#status} will be set to 404
     *
     * @param gameId a number matching the {@link KalahGame#gameId}.
     * @return {@link KalahGameResponse}
     */
    KalahGameResponse loadGame(long gameId);

    /**
     * Checks if the requested pit contains at least 1 stone, will return false if not.
     *
     * @param request a valid {@link KalahMoveRequest} which contains the {@link KalahMoveRequest#pit} to be validated
     * @param game    a {@link KalahGame} representing the game containing the pit to be validated.
     * @return {@link Boolean}
     */
    boolean checkStartingPitValidity(KalahMoveRequest request, KalahGame game);

    /**
     * Processes the requested move.  Validation should be performed via {@link GameService#validateAndProcessMove(KalahMoveRequest)} beforehand.
     *
     * @param request a valid {@link KalahMoveRequest}
     * @param game    a {@link KalahGame}
     * @return a {@link KalahGameResponse}
     */
    KalahGameResponse processMove(KalahMoveRequest request, KalahGame game);

    /**
     * Validates that both of the requested players exist and if so creates a new game.
     * If either or both players are missing will return status 400, otherwise returns the new game.
     *
     * @param request a {@link KalahCreateGameRequest} containing valid player id's
     * @return a {@link KalahGameResponse}
     */
    KalahGameResponse checkRequestAndCreateGame(KalahCreateGameRequest request);

    /**
     * Validates that the requested move request is legitimate and if so calls {@link GameService#processMove(KalahMoveRequest, KalahGame)}.
     * Will fail and return status 400 for the following errors:
     * Game not found.
     * Either or both players not found.
     * Player not playing this game.
     * Not correct players move.
     * Game is already ended.
     * Success will result in a a call to {@link GameService#processMove(KalahMoveRequest, KalahGame)}
     *
     * @param request a valid {@link KalahMoveRequest}
     * @return {@link KalahGameResponse}
     */
    KalahGameResponse validateAndProcessMove(KalahMoveRequest request);

    /**
     * Returns a list of all games the given player is participating in.
     * @param playerId the identifier of the {@link Person} you are attempting ot find games for.
     * @return a {@link List} of {@link KalahGame} that {@link Person} is participating in.
     */
    List<KalahGame> findGamesByPlayerId(BigInteger playerId);
}
