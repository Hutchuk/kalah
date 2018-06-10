package com.hutch.kalah.service.impl;

import com.hutch.kalah.domain.PlaceStonesOutput;
import com.hutch.kalah.entity.KalahBoard;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.service.StoneService;
import com.hutch.kalah.testutil.TestGameUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class StoneServiceImplTest {

    @Autowired
    StoneServiceImpl stoneService;

    private KalahGame game;

    @Before
    public void setUp() {

        Person pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));
        Person dave = new Person("Dave");
        dave.setPersonId(BigInteger.valueOf(2));

        this.game = TestGameUtil.createGame(pete, dave);
        this.game.setGameId(1);
    }

    @Test
    public void isPlayerOneCurrentMover_givenPlayerOneCurrent_shouldReturnTrue() {

        boolean playerOneCurrentMover = stoneService.isPlayerOneCurrentMover(game);

        assertThat(playerOneCurrentMover).isTrue();
    }

    @Test
    public void isPlayerOneCurrentMover_givenPlayerOneNotCurrent_shouldReturnFalse() {

        game.setCurrentPlayer(game.getPlayerTwo());

        boolean playerOneCurrentMover = stoneService.isPlayerOneCurrentMover(game);

        assertThat(playerOneCurrentMover).isFalse();
    }

    @Test
    public void incrementPit_givenPlayerOne_shouldMakePlayerOnePitOne7() {

        assertThat(game.getBoard().getPlayerOnePits().get(1)).isEqualTo(6);
        assertThat(game.getBoard().getPlayerOnePits().get(1)).isEqualTo(6);

        stoneService.incrementPit(game, 1, true);

        assertThat(game.getBoard().getPlayerOnePits().get(1)).isEqualTo(7);
        assertThat(game.getBoard().getPlayerTwoPits().get(1)).isEqualTo(6);
    }

    @Test
    public void incrementPit_givenPlayerTwo_shouldMakePlayerOnePitTwo7() {

        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(6);
        assertThat(game.getBoard().getPlayerTwoPits().get(2)).isEqualTo(6);

        stoneService.incrementPit(game, 2, false);

        assertThat(game.getBoard().getPlayerTwoPits().get(2)).isEqualTo(7);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(6);
    }

    @Test
    public void incrementKalah_givenPlayerOne_shouldIncrementPlayerOneOnly() {
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
        stoneService.incrementKalah(game, true);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(1);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
    }

    @Test
    public void incrementKalah_givenPlayerTwo_shouldIncrementPlayerOneOnly() {
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
        stoneService.incrementKalah(game, false);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(1);
    }

    @Test
    public void finalPitWasEmpty_givenPlayerOneAndPitEmpty_shouldReturnTrue() {

        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 1);

        boolean isEmpty = stoneService.finalPitWasEmpty(game, 1, true);

        assertThat(isEmpty).isTrue();
    }

    @Test
    public void finalPitWasEmpty_givenPlayerOneAndPitNotEmpty_shouldReturnFalse() {

        boolean isEmpty = stoneService.finalPitWasEmpty(game, 1, true);

        assertThat(isEmpty).isFalse();
    }

    @Test
    public void finalPitWasEmpty_givenPlayerTwoAndPitEmpty_shouldReturnTrue() {

        game.getBoard().getPlayerTwoPits().compute(1, (k, v) -> 1);

        boolean isEmpty = stoneService.finalPitWasEmpty(game, 1, false);

        assertThat(isEmpty).isTrue();
    }

    @Test
    public void finalPitWasEmpty_givenPlayerTwoAndPitNotEmpty_shouldReturnFalse() {

        boolean isEmpty = stoneService.finalPitWasEmpty(game, 1, false);

        assertThat(isEmpty).isFalse();
    }

    @Test
    public void finalPitWasEmpty_givenPlayerTwoAndPitIsActuallyKalah_shouldReturnFalse() {

        boolean isEmpty = stoneService.finalPitWasEmpty(game, game.getBoardSize() + 1, false);

        assertThat(isEmpty).isFalse();
    }

    @Test
    public void doEmptyPitCapture_givenCurrentPlayerOneEndsOnHisSideLastPitEmpty_shouldCaptureOpponentPit() {

        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 1);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(1);
        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(6);

        stoneService.doEmptyPitCapture(game, 2, true);

        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(7);

    }

    @Test
    public void doEmptyPitCapture_givenCurrentPlayerOneNotOnHisSideLastPitEmpty_shouldNotCaptureOpponentPit() {

        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 1);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(1);
        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(6);

        stoneService.doEmptyPitCapture(game, 2, false);

        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(6);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(1);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);

    }

    @Test
    public void doEmptyPitCapture_givenCurrentPlayerTwoEndsOnHisSideLastPitEmpty_shouldCaptureOpponentPit() {

        game.getBoard().getPlayerTwoPits().compute(4, (k, v) -> 1);
        game.setCurrentPlayer(game.getPlayerTwo());

        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(3)).isEqualTo(6);
        assertThat(game.getBoard().getPlayerTwoPits().get(4)).isEqualTo(1);

        stoneService.doEmptyPitCapture(game, 4, false);

        assertThat(game.getBoard().getPlayerTwoPits().get(4)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(3)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(7);

    }

    @Test
    public void getOpposingPit_givenANumber_shouldReturnOpposingPit() {
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 1)).isEqualTo(6);
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 2)).isEqualTo(5);
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 3)).isEqualTo(4);
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 4)).isEqualTo(3);
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 5)).isEqualTo(2);
        assertThat(stoneService.getOpposingPit(game.getBoardSize(), 6)).isEqualTo(1);
    }

    @Test
    public void moveLeftoverStonesToKalah_givenMovingPlayerOne_shouldIncreasePlayerOneKalah() {

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);

        stoneService.moveLeftoverStonesToKalah(game, 25, 0);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(25);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
    }

    @Test
    public void moveLeftoverStonesToKalah_givenMovingPlayerTwo_shouldIncreasePlayerTwoKalah() {

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);

        stoneService.moveLeftoverStonesToKalah(game, 0, 25);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(25);
    }

    @Test
    public void emptyPits() {

        assertThat(game.getBoard().getPlayerOnePits().values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(36);
        assertThat(game.getBoard().getPlayerTwoPits().values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(36);

        stoneService.emptyPits(game);

        assertThat(game.getBoard().getPlayerOnePits().values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(0);
        assertThat(game.getBoard().getPlayerTwoPits().values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(0);
    }

    @Test
    public void getStonesAndEmptyStartingPit_givenProcessingPlayerOne_shouldAffectPlayerOnePit() {

        int stones = stoneService.getStonesAndEmptyStartingPit(game, true, 1);

        assertThat(stones).isEqualTo(6);
        assertThat(game.getBoard().getPlayerOnePits().get(1)).isEqualTo(0);
    }

    @Test
    public void getStonesAndEmptyStartingPit_givenProcessingPlayerTwo_shouldAffectPlayerTwoPit() {

        int stones = stoneService.getStonesAndEmptyStartingPit(game, false, 3);

        assertThat(stones).isEqualTo(6);
        assertThat(game.getBoard().getPlayerTwoPits().get(3)).isEqualTo(0);
    }

    @Test
    public void placeStones_givenStartingBoardAndStonesFromPit1PlayerOneTurnAndSide_shouldIncrementPitsAndEndOnKalah() {

        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 0);

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 2, true, 6);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(1);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(3)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(4)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(5)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(6)).isEqualTo(game.getBoardSize() + 1);
        assertThat(placeStonesOutput.isEndedOnKalah()).isTrue();
        assertThat(placeStonesOutput.getStones()).isEqualTo(0);
    }

    @Test
    public void placeStones_givenStartingBoardAndStonesFromPit1PlayerTwoTurnAndSide_shouldIncrementPitsAndEndOnKalah() {

        game.setCurrentPlayer(game.getPlayerTwo());
        game.getBoard().getPlayerTwoPits().compute(1, (k, v) -> 0);

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 2, false, 6);

        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(1);
        assertThat(game.getBoard().getPlayerTwoPits().get(2)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(3)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(4)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(6)).isEqualTo(game.getBoardSize() + 1);
        assertThat(placeStonesOutput.isEndedOnKalah()).isTrue();
        assertThat(placeStonesOutput.getStones()).isEqualTo(0);
    }

    @Test
    public void placeStones_givenStartingBoardAndStonesFromPit2PlayerOneTurnAndSide_shouldIncrementPitsAndReturn1Stone() {

        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 0);

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 3, true, 6);

        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(1);
        assertThat(game.getBoard().getPlayerOnePits().get(3)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(4)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(5)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerOnePits().get(6)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerTwo());
        assertThat(placeStonesOutput.isEndedOnKalah()).isFalse();
        assertThat(placeStonesOutput.getStones()).isEqualTo(1);
    }

    @Test
    public void placeStones_givenCaptureScenario_shouldCaptureOpponent() {
        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 0);
        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 0);

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 2, true, 1);

        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(0);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(7);
        assertThat(placeStonesOutput.isEndedOnKalah()).isFalse();
        assertThat(placeStonesOutput.getStones()).isEqualTo(0);
    }

    @Test
    public void placeStones_givenLandingOnEmptySquareButNotPlayersMove_shouldNotCapture() {

        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 0);
        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 0);
        game.setCurrentPlayer(game.getPlayerTwo());

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 2, true, 1);

        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(game.getBoardSize());
        assertThat(game.getBoard().getPlayerOnePits().get(2)).isEqualTo(1);
        assertThat(game.getBoard().getPlayerOneKalah()).isEqualTo(0);
        assertThat(placeStonesOutput.isEndedOnKalah()).isFalse();
        assertThat(placeStonesOutput.getStones()).isEqualTo(0);
    }

    @Test
    public void placeStones_givenPlayerOneTurnOnPlayerTwoSide_shouldNotIncrementPlayerTwoKalah() {

        PlaceStonesOutput placeStonesOutput = stoneService.placeStones(game, 1, false, 8);

        assertThat(game.getBoard().getPlayerTwoPits().get(1)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(2)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(3)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(4)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(5)).isEqualTo(game.getBoardSize() + 1);
        assertThat(game.getBoard().getPlayerTwoPits().get(6)).isEqualTo(game.getBoardSize() + 1);

        assertThat(game.getBoard().getPlayerTwoKalah()).isEqualTo(0);
        assertThat(placeStonesOutput.isEndedOnKalah()).isFalse();
        assertThat(placeStonesOutput.getStones()).isEqualTo(2);
    }

    @TestConfiguration
    static class GameServiceImplTestContextConfiguration {

        @Bean
        public StoneService stoneService() {
            return new StoneServiceImpl();
        }
    }

}