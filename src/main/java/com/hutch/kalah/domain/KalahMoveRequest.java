package com.hutch.kalah.domain;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.google.common.base.MoreObjects.toStringHelper;

public class KalahMoveRequest {

    @NotNull
    private long gameId;
    @NotNull
    private BigInteger movingPlayerId;
    @NotNull
    private int pit;

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public BigInteger getMovingPlayerId() {
        return movingPlayerId;
    }

    public void setMovingPlayerId(BigInteger movingPlayerId) {
        this.movingPlayerId = movingPlayerId;
    }

    public int getPit() {
        return pit;
    }

    public void setPit(int pit) {
        this.pit = pit;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("gameId", gameId)
                .add("movingPlayerId", movingPlayerId)
                .add("pit", pit)
                .toString();
    }
}
