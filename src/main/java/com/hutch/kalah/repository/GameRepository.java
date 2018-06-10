package com.hutch.kalah.repository;

import com.hutch.kalah.entity.KalahGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface GameRepository extends JpaRepository<KalahGame, Long> {

    List<KalahGame> findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals(BigInteger playerId, BigInteger playerId2);
}
