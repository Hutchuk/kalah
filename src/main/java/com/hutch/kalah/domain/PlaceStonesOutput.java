package com.hutch.kalah.domain;

import com.hutch.kalah.entity.KalahGame;

import static com.google.common.base.MoreObjects.toStringHelper;

public class PlaceStonesOutput {

    private KalahGame game;
    private int stones;
    private int endPit;
    private boolean endedOnKalah;

    public boolean isEndedOnKalah() {
        return endedOnKalah;
    }

    public void setEndedOnKalah(boolean endedOnKalah) {
        this.endedOnKalah = endedOnKalah;
    }

    public int getEndPit() {
        return endPit;
    }

    public void setEndPit(int endPit) {
        this.endPit = endPit;
    }

    public KalahGame getGame() {
        return game;
    }

    public void setGame(KalahGame game) {
        this.game = game;
    }

    public int getStones() {
        return stones;
    }

    public void setStones(int stones) {
        this.stones = stones;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("game", game)
                .add("stones", stones)
                .add("endPit", endPit)
                .add("endedOnKalah", endedOnKalah)
                .toString();
    }
}
