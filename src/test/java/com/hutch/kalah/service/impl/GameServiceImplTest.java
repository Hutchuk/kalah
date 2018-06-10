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
import com.hutch.kalah.service.PersonService;
import com.hutch.kalah.testutil.TestGameUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GameServiceImplTest {

    @Autowired
    private GameServiceImpl gameService;

    @MockBean
    private GameRepository mockGameRepository;

    @MockBean
    private StoneServiceImpl mockStoneService;

    @MockBean
    private PersonService mockPersonService;

    private KalahGame game;
    private KalahMoveRequest kalahMoveRequest;
    private PlaceStonesOutput placeStonesOutput;
    private KalahCreateGameRequest kalahCreateGameRequest;
    private Person dave;
    private Person pete;
    private Person emma;

    @Before
    public void setUp() {


        pete = new Person("Pete");
        pete.setPersonId(BigInteger.valueOf(1));
        dave = new Person("Dave");
        dave.setPersonId(BigInteger.valueOf(2));
        emma = new Person("Emma");
        dave.setPersonId(BigInteger.valueOf(3));

        game = TestGameUtil.createGame(pete, dave);
        game.setGameId(1);

        placeStonesOutput = new PlaceStonesOutput();
        placeStonesOutput.setGame(game);

        kalahMoveRequest = new KalahMoveRequest();
        kalahMoveRequest.setGameId(1);
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(1));
        kalahMoveRequest.setPit(1);

        kalahCreateGameRequest = new KalahCreateGameRequest();
        kalahCreateGameRequest.setPlayerOneId(BigInteger.valueOf(1));
        kalahCreateGameRequest.setPlayerTwoID(BigInteger.valueOf(2));

        Optional<Person> optionalPete = Optional.of(pete);
        Optional<Person> optionalDave = Optional.of(dave);
        Optional<Person> optionalEmma = Optional.of(emma);
        Optional<Person> optionalEmptyPerson = Optional.empty();

        Optional<KalahGame> optionalGame = Optional.of(game);
        Optional<KalahGame> optionalEmptyGame = Optional.empty();

        when(mockPersonService.findbyId(BigInteger.valueOf(1))).thenReturn(optionalPete);
        when(mockPersonService.findbyId(BigInteger.valueOf(2))).thenReturn(optionalDave);
        when(mockPersonService.findbyId(BigInteger.valueOf(3))).thenReturn(optionalEmma);
        when(mockPersonService.findbyId(BigInteger.valueOf(20))).thenReturn(optionalEmptyPerson);


        when(mockGameRepository.save(any())).thenReturn(game);
        when(mockGameRepository.findById(1L)).thenReturn(optionalGame);
        when(mockGameRepository.findById(20L)).thenReturn(optionalEmptyGame);
        when(mockStoneService.placeStones(any(KalahGame.class), anyInt(), anyBoolean(), anyInt())).thenReturn(placeStonesOutput);

    }


    @Test
    public void createGame_GivenTwoPeople_shouldReturnGame() {

        KalahGame game = gameService.createGame(pete, dave);

        assertThat(game).isNotNull();
        assertThat(game.getPlayerOne()).isEqualTo(pete);
        assertThat(game.getPlayerTwo()).isEqualTo(dave);
        assertThat(game.getBoard().getPlayerOnePits().size()).isEqualTo(6);
        assertThat(game.getCurrentPlayer()).isEqualTo(pete);

    }

    @Test
    public void saveGame_givenGame_shouldSave() {

        KalahGame newGame = new KalahGame();

        KalahGame game = gameService.saveGame(newGame);

        assertThat(game).isNotNull();

        verify(mockGameRepository, times(1)).save(newGame);
    }


    @Test
    public void loadGame_givenExistingId_shouldLoadAndReturn200() {

        KalahGameResponse response = gameService.loadGame(1L);

        assertThat(response.getKalahGame()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);

        verify(mockGameRepository, times(1)).findById(1L);
    }

    @Test
    public void loadGame_givenIdDoesntExist_shouldReturn404() {

        KalahGameResponse response = gameService.loadGame(20L);

        assertThat(response.getKalahGame()).isNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(mockGameRepository, times(1)).findById(20L);
    }

    @Test
    public void checkStartingPitValidity_givenValidPlayerOnePit_shouldReturnTrue() {

        boolean moveValid = gameService.checkStartingPitValidity(kalahMoveRequest, game);

        assertThat(moveValid).isTrue();
    }

    @Test
    public void checkStartingPitValidity_givenValidPlayerTwoPit_shouldReturnTrue() {
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(2));
        game.setCurrentPlayer(game.getPlayerTwo());
        boolean moveValid = gameService.checkStartingPitValidity(kalahMoveRequest, game);

        assertThat(moveValid).isTrue();
    }

    @Test
    public void checkStartingPitValidity_givenInvalidPit_shouldReturnFalse() {

        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 0);
        boolean moveValid = gameService.checkStartingPitValidity(kalahMoveRequest, game);

        assertThat(moveValid).isFalse();
    }

    @Test
    public void isPlayerOneCurrentMover_givenPlayerOneCurrent_shouldReturnTrue() {

        boolean playerOneCurrentMover = gameService.isPlayerOneCurrentMover(game);

        assertThat(playerOneCurrentMover).isTrue();
    }

    @Test
    public void isPlayerOneCurrentMover_givenPlayerOneNotCurrent_shouldReturnFalse() {

        game.setCurrentPlayer(game.getPlayerTwo());
        boolean playerOneCurrentMover = gameService.isPlayerOneCurrentMover(game);

        assertThat(playerOneCurrentMover).isFalse();
    }

    @Test
    public void setEndStatusIfApplicable_givenGameOverPlayerOneHighScore_shouldReturnGameOverPlayerOneWins() {

        game.setGameOver(true);
        game.getBoard().setPlayerOneKalah(10);
        KalahGameResponse response = new KalahGameResponse();
        placeStonesOutput.setGame(game);

        gameService.setEndStatusIfApplicable(game, placeStonesOutput, response);

        assertThat(response.getStatusMessage()).isEqualToIgnoringCase(String.format("Game over, %s has won", game.getPlayerOne().getName()));
        assertThat(game.getWinner()).isEqualTo(game.getPlayerOne());
        assertThat(game.getPlayerOne().getVictories()).isEqualTo(1);
        assertThat(game.getPlayerTwo().getLosses()).isEqualTo(1);

    }

    @Test
    public void setEndStatusIfApplicable_givenGameOverPlayerTwoHighScore_shouldReturnGameOverPlayerTwoWins() {

        game.setGameOver(true);
        game.getBoard().setPlayerTwoKalah(10);
        placeStonesOutput.setGame(game);
        KalahGameResponse response = new KalahGameResponse();


        gameService.setEndStatusIfApplicable(game, placeStonesOutput, response);

        assertThat(response.getStatusMessage()).isEqualToIgnoringCase(String.format("Game over, %s has won", game.getPlayerTwo().getName()));
        assertThat(game.getWinner()).isEqualTo(game.getPlayerTwo());
        assertThat(game.getPlayerTwo().getVictories()).isEqualTo(1);
        assertThat(game.getPlayerOne().getLosses()).isEqualTo(1);
    }

    @Test
    public void setEndStatusIfApplicable_givenGameNotOverAndNoKalah_shouldReturnPlayerTwoTurn() {

        KalahGameResponse response = new KalahGameResponse();
        placeStonesOutput.setGame(game);

        gameService.setEndStatusIfApplicable(game, placeStonesOutput, response);

        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerTwo());
        assertThat(response.getStatusMessage()).isNullOrEmpty();
    }

    @Test
    public void setEndStatusIfApplicable_givenGameNotOverAndEndedOnKalah_shouldReturnPlayerOneTurn() {

        KalahGameResponse response = new KalahGameResponse();
        placeStonesOutput.setEndedOnKalah(true);
        placeStonesOutput.setGame(game);

        gameService.setEndStatusIfApplicable(game, placeStonesOutput, response);

        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerOne());
        assertThat(response.getStatusMessage()).isEqualToIgnoringCase(KalahMessage.MOVE_AGAIN.getMessage());
    }

    @Test
    public void changePlayerTurn_givenPlayerOneTurn_shouldBePlayerTwoTurn() {

        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerOne());
        gameService.changePlayerTurn(game);
        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerTwo());
    }

    @Test
    public void changePlayerTurn_givenPlayerTwoTurn_shouldBePlayerOneTurn() {

        game.setCurrentPlayer(game.getPlayerTwo());
        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerTwo());
        gameService.changePlayerTurn(game);
        assertThat(game.getCurrentPlayer()).isEqualTo(game.getPlayerOne());
    }

    @Test
    public void checkForGameOverAndMoveStonesToFinish_givenPitsEmpty_expectGameOver() {

        game.getBoard().getPlayerOnePits().replaceAll((k, v) -> 0);

        gameService.checkForGameOverAndMoveStonesToFinish(game);

        assertThat(game.isGameOver()).isTrue();

        verify(mockStoneService, times(1)).moveLeftoverStonesToKalah(any(KalahGame.class), anyInt(), anyInt());
    }

    @Test
    public void checkForGameOverAndMoveStonesToFinish_givenPitsNotEmpty_expectGameNotOver() {

        gameService.checkForGameOverAndMoveStonesToFinish(game);

        assertThat(game.isGameOver()).isFalse();

        verify(mockStoneService, times(0)).moveLeftoverStonesToKalah(any(KalahGame.class), anyInt(), anyInt());
    }

    @Test
    public void getPitsTotal() {

        int total = gameService.getPitsTotal(game.getBoard().getPlayerOnePits());
        assertThat(total).isEqualTo(36);
    }

    @Test
    public void processMove_givenEndGameScenario_shouldEndGame() {

        //Empty pits, set 1 to pit 1, game should end if that pit is chosen.
        game.getBoard().getPlayerOnePits().replaceAll((k, v) -> 0);
        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 1);

        when(mockStoneService.getStonesAndEmptyStartingPit(any(KalahGame.class), anyBoolean(), anyInt())).thenCallRealMethod();

        placeStonesOutput.setStones(0);
        placeStonesOutput.setEndPit(2);
        placeStonesOutput.setGame(game);

        gameService.processMove(kalahMoveRequest, game);

        assertThat(game.isGameOver()).isTrue();
    }

    @Test
    public void checkRequestAndCreateGame_givenPlayerOneDoesntExist_shouldReturn400() {

        kalahCreateGameRequest.setPlayerOneId(BigInteger.valueOf(20));

        KalahGameResponse response = gameService.checkRequestAndCreateGame(kalahCreateGameRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(20));
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(2));
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void checkRequestAndCreateGame_givenPlayerTwoDoesntExist_shouldReturn400() {

        kalahCreateGameRequest.setPlayerTwoID(BigInteger.valueOf(20));

        KalahGameResponse response = gameService.checkRequestAndCreateGame(kalahCreateGameRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(20));
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(1));
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void checkRequestAndCreateGame_givenBothPlayersExist_shouldCreateGameAndReturn201() {

        KalahGameResponse response = gameService.checkRequestAndCreateGame(kalahCreateGameRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED);
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(1));
        verify(mockPersonService, times(1)).findbyId(BigInteger.valueOf(2));
        verify(mockGameRepository, times(1)).save(any(KalahGame.class));
    }

    @Test
    public void getPlayerIdList_givenGameWithPlayers_shouldReturnListWithPlayers() {

        KalahGame game = TestGameUtil.createGame(pete, dave);

        List<BigInteger> ids = gameService.getPlayerIdList(game);

        assertThat(ids).containsAll(Arrays.asList(pete.getPersonId(), dave.getPersonId()));
    }

    @Test
    public void validateAndProcessMove_givenMovingPlayerNotFound_shouldReturn400() {
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(20));

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.PLAYER_NOT_FOUND.getMessage());
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void validateAndProcessMove_givenMovingPlayerExistsButNotInGame_shouldReturn400() {
        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(3));

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.INVALID_PLAYER.getMessage());
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void validateAndProcessMove_givenMovingPlayerExistsButGameDoesnt_shouldReturn400() {
        kalahMoveRequest.setGameId(20L);

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.GAME_NOT_FOUND.getMessage());
        verify(mockPersonService, times(0)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void validateAndProcessMove_givenPlayersAndGameLegitButGameOver_shouldReturn400() {
        game.setGameOver(true);

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.GAME_OVER_ERROR.getMessage());
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void validateAndProcessMove_givenPlayersAndGameLegitButNotPlayersTurn_shouldReturn400() {

        kalahMoveRequest.setMovingPlayerId(BigInteger.valueOf(2));

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.NOT_PLAYERS_MOVE.getMessage());
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }

    @Test
    public void validateAndProcessMove_givenPlayersAndGameLegitButStartingPitInvalid_shouldReturn400() {

        kalahMoveRequest.setPit(1);
        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 0);

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getErrorMessage()).contains(KalahMessage.INVALID_MOVE.getMessage());
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(0)).save(any(KalahGame.class));
    }


    @Test
    public void validateAndProcessMove_givenAllLegit_shouldReturn200AndGame() {

        game.getBoard().getPlayerOnePits().compute(1, (k, v) -> 1);
        game.getBoard().getPlayerOnePits().compute(2, (k, v) -> 0);
        when(mockStoneService.getStonesAndEmptyStartingPit(any(KalahGame.class), anyBoolean(), anyInt())).thenCallRealMethod();

        KalahGameResponse response = gameService.validateAndProcessMove(kalahMoveRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getErrorMessage()).isNullOrEmpty();
        verify(mockPersonService, times(1)).findbyId(any(BigInteger.class));
        verify(mockGameRepository, times(1)).findById(anyLong());
        verify(mockGameRepository, times(1)).save(any(KalahGame.class));
    }

    @Test
    public void findGamesByPlayerId_givenGameExistsForPlayer_shouldFindGame(){
        KalahGame game = TestGameUtil.createGame(pete, dave);

        gameService.saveGame(game);

        List<KalahGame> games = gameService.findGamesByPlayerId(dave.getPersonId());

        assertThat(games.contains(game));

        verify(mockGameRepository, times(1)).findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals(dave.getPersonId(), dave.getPersonId());
    }

    @Test
    public void findGamesByPlayerId_givenGameDoesntExistsForPlayer_shouldNotFindGames(){
        KalahGame game = TestGameUtil.createGame(pete, dave);
        BigInteger playerNotInGamesId = BigInteger.valueOf(99);
        gameService.saveGame(game);

        List<KalahGame> games = gameService.findGamesByPlayerId(playerNotInGamesId);

        assertThat(games).isEmpty();

        verify(mockGameRepository, times(1)).findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals(playerNotInGamesId, playerNotInGamesId);
    }
}
