package com.hutch.kalah.testutil;

import com.hutch.kalah.entity.KalahBoard;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;

public class TestGameUtil {


    public static KalahGame createGame(Person player1, Person player2) {

        KalahGame kalahGame = new KalahGame();
        kalahGame.setBoard(new KalahBoard());
        kalahGame.setPlayerOne(player1);
        kalahGame.setPlayerTwo(player2);
        kalahGame.setCurrentPlayer(player1);
        kalahGame.initialise(6, 6);


        return kalahGame;
    }
}
