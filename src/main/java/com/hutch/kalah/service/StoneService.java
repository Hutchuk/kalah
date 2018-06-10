package com.hutch.kalah.service;

import com.hutch.kalah.domain.PlaceStonesOutput;
import com.hutch.kalah.entity.KalahGame;

/**
 * Allows for manipulation of stone amounts on the board.
 */
public interface StoneService {
    /**
     * Main logic for placing stones in order, starts with the pit number passed and places a stone into each pit and potentially into the Kalah.
     * @param game a {@link  KalahGame}
     * @param pit the pit where the first stone is to be placed
     * @param currentlyProcessingPlayerOnePits an awkwardly named boolean indicating whether the south pits are currently being processed.
     * @param stones the number of stones to place
     * @return a {@link PlaceStonesOutput} containing the remaining stones and also {@link PlaceStonesOutput#endedOnKalah} which allows for free turn checking.
     */
    PlaceStonesOutput placeStones(KalahGame game, int pit, boolean currentlyProcessingPlayerOnePits, int stones);

    /**
     * To be called when the game has ended to move the opposing players stones to their Kalah.
     * @param game a {@link  KalahGame}
     * @param totalPlayer1 Total number of stones in player 1 pits
     * @param totalPlayer2 Total number of stones in player 2 pits.
     * @return an updated {@link KalahGame}
     */
    KalahGame moveLeftoverStonesToKalah(KalahGame game, int totalPlayer1, int totalPlayer2);

    /**
     * Set the number of stones in the chosen pit to 0 and returns the number of stones picked up.
     * To be called at the start of each move.
     * @param game a {@link  KalahGame}
     * @param currentlyProcessingPlayerOnePits an awkwardly named boolean indicating whether the south pits are currently being processed.
     * @param pit the pit which is to be emptied
     * @return the number of stones to be played.
     */
    int getStonesAndEmptyStartingPit(KalahGame game, boolean currentlyProcessingPlayerOnePits, int pit);

}
