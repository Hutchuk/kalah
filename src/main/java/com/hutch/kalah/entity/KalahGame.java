package com.hutch.kalah.entity;

import javax.persistence.*;

import static com.google.common.base.MoreObjects.toStringHelper;

@Entity
public class KalahGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;
    @ManyToOne(cascade = CascadeType.ALL)
    private Person playerOne;
    @ManyToOne(cascade = CascadeType.ALL)
    private Person playerTwo;
    @ManyToOne(cascade = CascadeType.ALL)
    private Person currentPlayer;
    @ManyToOne(cascade = CascadeType.ALL)
    private Person winner;
    @ManyToOne(cascade = CascadeType.ALL)
    private KalahBoard board;
    private int boardSize;


    private boolean gameOver = false;

    public KalahGame() {
    }

    public void initialise(int seeds, int boardSize) {
        this.board = new KalahBoard();
        this.boardSize = boardSize;

        for (int i = 1; i <= boardSize; i++) {
            this.board.getPlayerOnePits().put(i, seeds);
            this.board.getPlayerTwoPits().put(i, seeds);
        }
    }

    public Person getWinner() {
        return winner;
    }

    public void setWinner(Person winner) {
        this.winner = winner;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Person getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Person playerOne) {
        this.playerOne = playerOne;
    }

    public Person getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Person playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Person getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Person currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public KalahBoard getBoard() {
        return board;
    }

    public void setBoard(KalahBoard board) {
        this.board = board;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("gameId", gameId)
                .add("playerOne", playerOne)
                .add("playerTwo", playerTwo)
                .add("currentPlayer", currentPlayer)
                .add("winner", winner)
                .add("board", board)
                .add("boardSize", boardSize)
                .add("gameOver", gameOver)
                .toString();
    }
}
