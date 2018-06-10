package com.hutch.kalah.domain;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

import static com.google.common.base.MoreObjects.toStringHelper;

public class KalahCreateGameRequest {

    @NotNull
    private BigInteger playerOneId;
    @NotNull
    private BigInteger playerTwoId;

    public BigInteger getPlayerOneId() {
        return playerOneId;
    }

    public void setPlayerOneId(BigInteger playerOneId) {
        this.playerOneId = playerOneId;
    }

    public BigInteger getPlayerTwoId() {
        return playerTwoId;
    }

    public void setPlayerTwoID(BigInteger playerTwoId) {
        this.playerTwoId = playerTwoId;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("playerOneId", playerOneId)
                .add("playerTwoId", playerTwoId)
                .toString();
    }
}
