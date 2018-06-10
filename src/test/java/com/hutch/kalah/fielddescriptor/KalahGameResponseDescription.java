package com.hutch.kalah.fielddescriptor;

import com.hutch.kalah.entity.KalahBoard;
import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.math.BigInteger;
import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class KalahGameResponseDescription {

    private KalahGameResponseDescription() {
    }

    public static FieldDescriptor[] KalahGameResponse() {
        return new FieldDescriptor[]{
                fieldWithPath("kalahGame").description("The KalahGameList Object containing the board and players.").type(KalahGame.class).optional(),
                fieldWithPath("statusMessage").description("Message detailing the result of the last move.").type(String.class).optional(),
                fieldWithPath("errorMessage").description("Message detailing any errors.").type(String.class).optional(),
                fieldWithPath("kalahGame.gameId").description("The primary key of the KalahGameList.").type(Long.class).optional(),
                fieldWithPath("kalahGame.playerOne").description("The Person who is player one.").type(String.class).optional(),
                fieldWithPath("kalahGame.playerOne.personId").description("The primary key for Player One.").type(BigInteger.class).optional(),
                fieldWithPath("kalahGame.playerOne.name").description("The name of Player One.").type(String.class).optional(),
                fieldWithPath("kalahGame.playerOne.victories").description("How many victories Player One has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.playerOne.losses").description("How many losses Player One has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.playerOne.draws").description("How many draws Player One has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.playerTwo").description("The Person who is player two.").type(String.class).optional(),
                fieldWithPath("kalahGame.playerTwo.personId").description("The primary key for Player Two.").type(BigInteger.class).optional(),
                fieldWithPath("kalahGame.playerTwo.name").description("The name of Player Two.").type(String.class).optional(),
                fieldWithPath("kalahGame.playerTwo.victories").description("How many victories Player Two has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.playerTwo.losses").description("How many losses Player Two has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.playerTwo.draws").description("How many draws Player Two has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.currentPlayer").description("The Person who is due to take their turn").type(Person.class).optional(),
                fieldWithPath("kalahGame.currentPlayer.personId").description("The primary key for the current player.").type(BigInteger.class).optional(),
                fieldWithPath("kalahGame.currentPlayer.name").description("The name of the current player.").type(String.class).optional(),
                fieldWithPath("kalahGame.currentPlayer.victories").description("How many victories the current player has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.currentPlayer.losses").description("How many losses the current player has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.currentPlayer.draws").description("How many draws the current player has.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board").description("The KalahBoard.").type(KalahBoard.class).optional(),
                fieldWithPath("kalahGame.board.boardId").description("The Primary key of the KalahBoard.").type(Long.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits").description("The object holding the pits for player one.").type(Map.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.1").description("Player One Pit 1.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.2").description("Player One Pit 2.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.3").description("Player One Pit 3.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.4").description("Player One Pit 4.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.5").description("Player One Pit 5.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOnePits.6").description("Player One Pit 6.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits").description("The object holding the pits for player two.").type(Map.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.1").description("Player Two Pit 1.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.2").description("Player Two Pit 2.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.3").description("Player Two Pit 3.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.4").description("Player Two Pit 4.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.5").description("Player Two Pit 5.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoPits.6").description("Player Two Pit 6.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerOneKalah").description("Player One's Kalah.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.board.playerTwoKalah").description("Player Two's Kalah.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.boardSize").description("The size of the KalahBoard.").type(Integer.class).optional(),
                fieldWithPath("kalahGame.gameOver").description("Boolean to indicate if the game is over or not.").type(Boolean.class).optional()};
    }
}
