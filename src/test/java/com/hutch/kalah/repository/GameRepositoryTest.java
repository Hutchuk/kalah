package com.hutch.kalah.repository;

import com.hutch.kalah.entity.KalahGame;
import com.hutch.kalah.entity.Person;
import com.hutch.kalah.testutil.TestGameUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals_givenPlayerHasGame_shouldreturnGame() {
        // Given
        Person pete = new Person("pete");
        Person dave = new Person("dave");

        KalahGame game = TestGameUtil.createGame(pete, dave);
        testEntityManager.persist(game);
        testEntityManager.flush();

        // When
        List<KalahGame> foundGames = gameRepository.findAllByPlayerOne_personIdEqualsOrPlayerTwo_personIdEquals(dave.getPersonId(), dave.getPersonId());

        // Then
        assertThat(foundGames).contains(game);
    }
}
