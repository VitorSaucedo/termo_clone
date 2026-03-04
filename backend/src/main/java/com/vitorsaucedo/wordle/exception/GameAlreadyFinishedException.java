package com.vitorsaucedo.wordle.exception;

import java.util.UUID;

public class GameAlreadyFinishedException extends RuntimeException {
    public GameAlreadyFinishedException(UUID sessionId) {
        super("A partida %s já foi encerrada.".formatted(sessionId));
    }
}