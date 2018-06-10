package com.hutch.kalah.domain.constant;

public enum KalahMessage {
    PLAYER_NOT_FOUND("Player not found: "),
    GAME_NOT_FOUND("Game not found: "),
    NOT_PLAYERS_MOVE("Not this players move: "),
    GAME_OVER_ERROR("Sorry this game is already completed"),
    INVALID_MOVE("There are no stones in this pit, please choose another"),
    TURN_OVER("Move completed successfully, next player's turn."),
    MOVE_AGAIN("Move ended on Kalah, move again."),
    INVALID_PLAYER("Player not playing this game: ");

    private String message;

    KalahMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
