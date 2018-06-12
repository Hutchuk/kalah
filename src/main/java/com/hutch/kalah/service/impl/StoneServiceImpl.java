package com.hutch.kalah.service.impl;

import com.hutch.kalah.domain.PlaceStonesOutput;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.service.StoneService;
import org.springframework.stereotype.Service;


/**
 * Implementation of the {@link StoneService} interface.
 */
@Service
public class StoneServiceImpl implements StoneService {
    /**
     * {@inheritDoc}
     */
    @Override
    public PlaceStonesOutput placeStones(KalahGame game, int startingPit, boolean currentlyProcessingPlayerOnePits, int stones) {
        PlaceStonesOutput output = new PlaceStonesOutput();

        for (int i = startingPit; i <= game.getBoardSize() && stones > 0; i++) {
            game = incrementPit(game, i, currentlyProcessingPlayerOnePits);
            stones--;
            if (stones > 0) {
                startingPit++;
            }
        }

        if (stones > 0 && startingPit > game.getBoardSize()) {
            if (currentlyProcessingPlayerOnePits == isPlayerOneCurrentMover(game)) {
                game = incrementKalah(game, currentlyProcessingPlayerOnePits);
                stones--;
            }
            if (stones == 0) {
                output.setEndedOnKalah(true);
            }
        }
        if (stones == 0) {
            output.setEndPit(startingPit);
            if (finalPitWasEmpty(game, startingPit, currentlyProcessingPlayerOnePits)) {
                game = doEmptyPitCapture(game, startingPit, currentlyProcessingPlayerOnePits);
            }
        }

        output.setGame(game);
        output.setStones(stones);

        return output;
    }

    boolean isPlayerOneCurrentMover(KalahGame game) {
        return game.getCurrentPlayer().getPersonId().equals(game.getPlayerOne().getPersonId());
    }

    KalahGame incrementPit(KalahGame game, int pit, boolean currentlyProcessingPlayerOnePits) {
        if (currentlyProcessingPlayerOnePits) {
            game.getBoard().getPlayerOnePits().compute(pit, (k, v) -> v + 1);
        } else {
            game.getBoard().getPlayerTwoPits().compute(pit, (k, v) -> v + 1);
        }
        return game;
    }

    KalahGame incrementKalah(KalahGame game, boolean currentlyProcessingPlayerOnePits) {
        if (currentlyProcessingPlayerOnePits) {
            game.getBoard().setPlayerOneKalah(game.getBoard().getPlayerOneKalah() + 1);
        } else {
            game.getBoard().setPlayerTwoKalah(game.getBoard().getPlayerTwoKalah() + 1);
        }
        return game;
    }

    boolean finalPitWasEmpty(KalahGame game, int pit, boolean currentlyProcessingPlayerOnePits) {
        if (pit <= game.getBoardSize()) {
            if (currentlyProcessingPlayerOnePits) {
                return game.getBoard().getPlayerOnePits().get(pit) == 1;
            } else {
                return game.getBoard().getPlayerTwoPits().get(pit) == 1;
            }
        }
        return false;
    }

    KalahGame doEmptyPitCapture(KalahGame game, int i, boolean currentlyProcessingPlayerOnePits) {
        int kalahTotal;
        int opposingPit;

        if (isPlayerOneCurrentMover(game) && currentlyProcessingPlayerOnePits) {
            kalahTotal = game.getBoard().getPlayerOneKalah();
            kalahTotal += game.getBoard().getPlayerOnePits().get(i);
            game.getBoard().getPlayerOnePits().put(i, 0);
            opposingPit = getOpposingPit(game.getBoardSize(), i);
            kalahTotal += game.getBoard().getPlayerTwoPits().get(opposingPit);
            game.getBoard().getPlayerTwoPits().put(opposingPit, 0);
            game.getBoard().setPlayerOneKalah(kalahTotal);
        } else if (!currentlyProcessingPlayerOnePits && !isPlayerOneCurrentMover(game)) {
            kalahTotal = game.getBoard().getPlayerTwoKalah();
            kalahTotal += game.getBoard().getPlayerTwoPits().get(i);
            game.getBoard().getPlayerTwoPits().put(i, 0);
            opposingPit = getOpposingPit(game.getBoardSize(), i);
            kalahTotal += game.getBoard().getPlayerOnePits().get(opposingPit);
            game.getBoard().getPlayerOnePits().put(opposingPit, 0);
            game.getBoard().setPlayerTwoKalah(kalahTotal);
        }

        return game;
    }

    Integer getOpposingPit(int boardSize, int i) {
        int total = boardSize + 1;
        return total - i;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KalahGame moveLeftoverStonesToKalah(KalahGame game, int totalPlayer1, int totalPlayer2) {
        int total;
        if (totalPlayer2 > 0) {
            total = totalPlayer2;
            total += game.getBoard().getPlayerTwoKalah();
            game.getBoard().setPlayerTwoKalah(total);
        } else {
            total = totalPlayer1;
            total += game.getBoard().getPlayerOneKalah();
            game.getBoard().setPlayerOneKalah(total);

        }
        game = emptyPits(game);
        return game;
    }

    KalahGame emptyPits(KalahGame game) {

        game.getBoard().getPlayerOnePits().replaceAll((k, v) -> 0);
        game.getBoard().getPlayerTwoPits().replaceAll((k, v) -> 0);

        return game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStonesAndEmptyStartingPit(KalahGame game, boolean currentlyProcessingPlayerOnePits, int pit) {
        int stones;
        if (currentlyProcessingPlayerOnePits) {
            stones = game.getBoard().getPlayerOnePits().get(pit);
            game.getBoard().getPlayerOnePits().compute(pit, (k, v) -> 0);
        } else {
            stones = game.getBoard().getPlayerTwoPits().get(pit);
            game.getBoard().getPlayerTwoPits().compute(pit, (k, v) -> 0);
        }
        return stones;
    }
}

