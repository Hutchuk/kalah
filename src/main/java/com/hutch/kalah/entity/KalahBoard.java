package com.hutch.kalah.entity;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.toStringHelper;

@Entity
public class KalahBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @ElementCollection(targetClass = Integer.class)
    @CollectionTable(name = "playerOnePits")
    @MapKeyColumn(name = "pit")
    @Column(name = "seeds")
    private Map<Integer, Integer> playerOnePits = new LinkedHashMap<>();

    @ElementCollection(targetClass = Integer.class)
    @CollectionTable(name = "playerTwoPits")
    @MapKeyColumn(name = "pit")
    @Column(name = "seeds")
    private Map<Integer, Integer> playerTwoPits = new LinkedHashMap<>();
    private int playerOneKalah = 0;
    private int playerTwoKalah = 0;

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public Map<Integer, Integer> getPlayerOnePits() {
        return playerOnePits;
    }

    public void setPlayerOnePits(Map<Integer, Integer> playerOnePits) {
        this.playerOnePits = playerOnePits;
    }

    public Map<Integer, Integer> getPlayerTwoPits() {
        return playerTwoPits;
    }

    public void setPlayerTwoPits(Map<Integer, Integer> playerTwoPits) {
        this.playerTwoPits = playerTwoPits;
    }

    public int getPlayerOneKalah() {
        return playerOneKalah;
    }

    public void setPlayerOneKalah(int playerOneKalah) {
        this.playerOneKalah = playerOneKalah;
    }

    public int getPlayerTwoKalah() {
        return playerTwoKalah;
    }

    public void setPlayerTwoKalah(int playerTwoKalah) {
        this.playerTwoKalah = playerTwoKalah;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("boardId", boardId)
                .add("playerOnePits", playerOnePits)
                .add("playerTwoPits", playerTwoPits)
                .add("playerOneKalah", playerOneKalah)
                .add("playerTwoKalah", playerTwoKalah)
                .toString();
    }
}
