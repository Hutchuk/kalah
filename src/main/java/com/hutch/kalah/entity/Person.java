package com.hutch.kalah.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;

import static com.google.common.base.MoreObjects.toStringHelper;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
public class Person extends ResourceSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger personId;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    @Column(name = "kalah_victories")
    private int victories = 0;

    @Column(name = "kalah_losses")
    private int losses;

    @Column(name = "kalah_draws")
    private int draws;


    public Person(@NotNull @Size(min = 2, max = 40) String name) {
        this.name = name;
    }

    public Person() {
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getVictories() {
        return victories;
    }

    public void setVictories(int victories) {
        this.victories = victories;
    }

    public BigInteger getPersonId() {
        return personId;
    }

    public void setPersonId(BigInteger personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void recordVictory() {
        this.victories++;
    }

    public void recordLoss() {
        this.losses++;
    }

    public void recordDraw() {
        this.draws++;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("personId", personId)
                .add("name", name)
                .add("victories", victories)
                .add("losses", losses)
                .add("draws", draws)
                .toString();
    }
}
