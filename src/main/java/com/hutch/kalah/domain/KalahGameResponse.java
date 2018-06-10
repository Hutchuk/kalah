package com.hutch.kalah.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hutch.kalah.entity.KalahGame;
import org.springframework.http.HttpStatus;

import static com.google.common.base.MoreObjects.toStringHelper;

public class KalahGameResponse {

    @JsonIgnore
    public HttpStatus status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String statusMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private KalahGame kalahGame;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public KalahGame getKalahGame() {
        return kalahGame;
    }

    public void setKalahGame(KalahGame kalahGame) {
        this.kalahGame = kalahGame;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("errorMessage", errorMessage)
                .add("statusMessage", statusMessage)
                .add("kalahGame", kalahGame)
                .add("status", status)
                .toString();
    }
}
